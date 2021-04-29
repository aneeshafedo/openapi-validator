package org.openapi.validator;

import com.moandjiezana.toml.Toml;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Validator {
    public static final String DISPLAY_EXTENSION = "x-display";
    public static final String LABEL = "label";
    public static final String ICON = "icon";
    public static final String ORG = "ballerinax";
    private static final String MD5 = "MD5";
    private static final java.util.logging.Logger LOGGER = Logger.getInstance();
    
    public static List<String> validate(String rootDir) throws OpenApiException, ValidatorException {
        String propertyFile = getPropertyFile(rootDir);
        List<String> updatedYamlFiles = new ArrayList<>();
        Properties props = new Properties();
        try (InputStream input = new FileInputStream(propertyFile)) {
            props.load(input);
            try (Stream<Path> walk = Files.walk(Paths.get(rootDir))) {
                List<String> fileList = walk.map(Path::toString).filter(f -> f.endsWith(FileType.YAML.getValue()) || f.endsWith(FileType.TOML.getValue())).collect(Collectors.toList());
                for (String file : fileList) {
                    String currentHash = props.getProperty(file);
                    try {
                        String newHash = generateChecksum(file);
                        if (!newHash.equals(currentHash)) {
                            if (file.endsWith(FileType.YAML.getValue())) {
                                validateYaml(file);
                                updatedYamlFiles.add(file);
                            } else {
                                validateToml(file);
                            }
                            props.setProperty(file, newHash);
                        }
                    } catch (NoSuchAlgorithmException ex) {
                        LOGGER.severe(ex.getMessage());
                    }
                }
            } catch (IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        } catch (IOException ex) {
            LOGGER.severe(ex.getMessage());
        }
        try (OutputStream output = new FileOutputStream(propertyFile)) {
            props.store(output, null);
        } catch (IOException ex) {
            LOGGER.severe(ex.getMessage());
        }
        return updatedYamlFiles;
    }

    private static String getPropertyFile(String rootDir) throws ValidatorException {
        String propertFilePath;
        File rootDirectory = new File(rootDir);
        if (rootDirectory.isDirectory()) {
            Optional<File> propertFile = Arrays.stream(Objects.requireNonNull(rootDirectory.listFiles())).filter(x -> x.getName().endsWith(FileType.PROPERTIES.getValue())).findFirst();
            if (propertFile.isPresent()) {
                propertFilePath = propertFile.get().getPath();
            } else throw new ValidatorException(ErrorMessages.propertyFileNotFound(rootDir));
        } else throw new ValidatorException(ErrorMessages.invalidRootDirectory(rootDir));
        return propertFilePath;
    }

    private static void validateYaml(String filePath) throws OpenApiException, ValidatorException, IOException {
        OpenAPI openApi = parseOpenAPIFile(filePath);
        validateDisplayAnnotation(openApi, filePath);
    }

    private static void validateDisplayAnnotation(OpenAPI openAPI, String filePath) throws ValidatorException {
        validateService(openAPI, filePath);
        validatePaths(openAPI, filePath);
        validateComponents(openAPI, filePath);
    }

    private static void validateService(OpenAPI openAPI, String filePath) throws ValidatorException {
        if (openAPI.getInfo() != null) {
            Info info = openAPI.getInfo();
            Map<String, Object> extensions = info.getExtensions();
            validateExtensions(extensions, filePath, true);
        }
    }

    private static void validatePaths(OpenAPI openAPI, String filePath) throws ValidatorException {
        if (openAPI.getPaths() != null) {
            io.swagger.v3.oas.models.Paths paths = openAPI.getPaths();

            for (Map.Entry<String, PathItem> path : paths.entrySet()) {
                Map<String, Object> extensions = path.getValue().getGet().getExtensions();
                validateExtensions(extensions, filePath, false);
            }
        }
    }

    private static void validateComponents(OpenAPI openAPI, String filePath) throws ValidatorException {
        if (openAPI.getComponents() != null) {
            Components components = openAPI.getComponents();
            if (components.getParameters() != null) {
                Map<String, Parameter> paramerters = components.getParameters();
                for (Map.Entry<String, Parameter> param : paramerters.entrySet()) {
                    Parameter paraVal = param.getValue();
                    Map<String, Object> extensions = paraVal.getExtensions();
                    validateExtensions(extensions, filePath, false);
                }
            }
        }
    }

    private static void validateExtensions(Map<String, Object> extensions, String filePath, boolean isService) throws ValidatorException {
        boolean isDisplayXAvailable = false;
        boolean isDisplayXLabelAvailable = false;
        boolean isDisplayXIconAvailable = false;
        if (extensions != null) {
            for (Map.Entry<String, Object> ext : extensions.entrySet()) {
                String extensionName = ext.getKey();
                if (extensionName.equals(DISPLAY_EXTENSION)) {
                    isDisplayXAvailable = true;
                    LinkedHashMap<String, String> extFields = (LinkedHashMap<String, String>) ext.getValue();
                    for (Map.Entry<String, String> field : extFields.entrySet()) {
                        String fieldName = field.getKey();
                        if (fieldName.equals(LABEL)) {
                            String labelVal = field.getValue();
                            if (!labelVal.isEmpty()) {
                                isDisplayXLabelAvailable = true;
                            }
                        } else if (fieldName.equals(ICON)) {
                            String icon = field.getValue();
                            if (!icon.isEmpty()) {
                                isDisplayXIconAvailable = true;
                            }
                        }
                    }
                    if (isService) {
                        if (!isDisplayXIconAvailable)
                            throw new ValidatorException(ErrorMessages.displayExtentionInvalid(filePath));
                    } else {
                        if (!isDisplayXLabelAvailable)
                            throw new ValidatorException(ErrorMessages.displayExtentionInvalid(filePath));
                    }
                }
            }
            if (!isDisplayXAvailable) throw new ValidatorException(ErrorMessages.displayExtentionNotFound(filePath));
        } else throw new ValidatorException(ErrorMessages.displayExtentionNotFound(filePath));
    }

    private static void validateToml(String filePath) throws ValidatorException, IOException {
        try (InputStream input = new FileInputStream(filePath)) {
            Toml toml = new Toml().read(input);
            validatePackageOrg(toml.getString(PackageInfo.ORG.getValue()), filePath);
            validatePackageName(toml.getString(PackageInfo.Name.getValue()), filePath);
            validatePackageVersion(toml.getString(PackageInfo.VERSION.getValue()), filePath);
        }
    }

    private static void validatePackageOrg(String packageOrg, String filePath) throws ValidatorException {
        if (!packageOrg.equals(ORG))
            throw new ValidatorException(ErrorMessages.invalidPackageOrg(filePath));
    }

    private static void validatePackageName(String packageName, String filePath) throws ValidatorException {
        boolean isValid = packageName.matches(RegexPatterns.NAME);
        if (!isValid)
            throw new ValidatorException(ErrorMessages.invalidPackageName(filePath));
    }

    private static void validatePackageVersion(String packageVersion, String filePath) throws ValidatorException {
        if (packageVersion == null||packageVersion.isEmpty()){
            throw new ValidatorException(ErrorMessages.invalidPackageVersion(filePath));
        }
    }

    private static OpenAPI parseOpenAPIFile(String definitionURI) throws OpenApiException, IOException {
        Path contractPath = Paths.get(definitionURI);
        if (!Files.exists(contractPath)) {
            throw new OpenApiException(ErrorMessages.invalidFilePath(definitionURI));
        }
        if (!(definitionURI.endsWith(FileType.YAML.getValue()) || definitionURI.endsWith(FileType.JSON.getValue()) || definitionURI.endsWith(FileType.YML.getValue()))) {
            throw new OpenApiException(ErrorMessages.invalidFile());
        }
        String openAPIFileContent = Files.readString(Paths.get(definitionURI));
        SwaggerParseResult parseResult = new OpenAPIV3Parser().readContents(openAPIFileContent);
        if (!parseResult.getMessages().isEmpty()) {
            throw new OpenApiException(ErrorMessages.parserException(definitionURI));
        }
        return parseResult.getOpenAPI();
    }

    private static String generateChecksum(String filePath) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance(MD5);
        md.update(Files.readAllBytes(Paths.get(filePath)));
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest).toUpperCase();
    }
}