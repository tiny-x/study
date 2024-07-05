
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.*;
import javax.tools.JavaFileObject.Kind;
import java.io.*;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.jar.JarEntry;

/**
 * @author xf.yefei
 */
public class JavaDynamicCompiler {

    private static void generateDiagnosticReport(
            DiagnosticCollector<JavaFileObject> collector, StringBuilder reporter) throws IOException {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = collector.getDiagnostics();
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
            JavaFileObject source = diagnostic.getSource();
            if (source != null) {
                reporter.append("Source: ").append(source.getName()).append('\n');
                reporter.append("Line ").append(diagnostic.getLineNumber()).append(": ")
                        .append(diagnostic.getMessage(Locale.ENGLISH)).append('\n');
                CharSequence content = source.getCharContent(true);
                BufferedReader reader = new BufferedReader(new StringReader(content.toString()));
                int i = 1;
                String line;
                while ((line = reader.readLine()) != null) {
                    reporter.append(i).append('\t').append(line).append('\n');
                    ++i;
                }
            } else {
                reporter.append("Source: (null)\n");
                reporter.append("Line ").append(diagnostic.getLineNumber()).append(": ")
                        .append(diagnostic.getMessage(Locale.ENGLISH)).append('\n');
            }
            reporter.append('\n');
        }
    }

    public static Class<?> compileClass(ClassLoader classLoader, String className, String content) {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        if (javaCompiler == null) {
            throw new RuntimeException("Not found system java compile");
        }
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        JavaFileObject javaFileObject = new InputStringJavaFileObject(className, content);
        StandardJavaFileManager standardFileManager = javaCompiler.getStandardFileManager(null, null, Charset.defaultCharset());
        InMemoryJavaFileManager fileManager = new InMemoryJavaFileManager(classLoader, standardFileManager);
        JavaCompiler.CompilationTask compilationTask = javaCompiler.getTask(null, fileManager, diagnostics, Collections.singletonList("-XDuseUnsharedTable"), null,
                Collections.singletonList(javaFileObject));
        if (Boolean.TRUE.equals(compilationTask.call())) {
            try {
                if (classLoader instanceof URLClassLoader) {
                    URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
                    return new CompiledClassLoader(classLoader, fileManager.getOutputs(), urlClassLoader.getURLs(), className).loadClass(className);
                } else {
                    return new CompiledClassLoader(classLoader, fileManager.getOutputs(), new URL[0], className).loadClass(className);
                }
            } catch (Exception ce) {
                throw new RuntimeException("compile class failed:" + className + ce);
            }
        } else {
            StringBuilder reporter = new StringBuilder(1024);
            reporter.append("Compilation failed.\n");
            try {
                generateDiagnosticReport(diagnostics, reporter);
            } catch (IOException e) {
                reporter.append("io exception:").append(e.getMessage());
            }
            throw new RuntimeException(reporter.toString());
        }
    }

    public static class CompiledClassLoader extends URLClassLoader {
        private final List<OutputClassJavaFileObject> files;

        private final String className;

        private CompiledClassLoader(ClassLoader parent, List<OutputClassJavaFileObject> files, URL[] urls, String className) {
            super(urls, parent);
            this.files = new ArrayList<>();
            this.files.addAll(files);
            this.className = className;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            for (OutputClassJavaFileObject file : files) {
                if (file.getClassName().equals(name)) {
                    byte[] bytes = file.getBytes();
                    return super.defineClass(name, bytes, 0, bytes.length);
                }
            }
            throw new ClassNotFoundException(name);
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            // 脚本不能让 parent 去加载，不然会同名类报错
            if (name.equals(className)) {
                return findClass(className);
            }
            return super.loadClass(name);
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            for (OutputClassJavaFileObject file : files) {
                if (file.toUri().getPath().equals("/" + name)) {
                    return new ByteArrayInputStream(file.getBytes());
                }
            }
            return null;
        }
    }

    /**
     * 将输出类保存在内存中
     */
    private static class OutputClassJavaFileObject extends SimpleJavaFileObject {
        private final ByteArrayOutputStream outputStream;
        private final String className;

        protected OutputClassJavaFileObject(String className, Kind kind) {
            super(URI.create("mem:///" + className.replace('.', '/') + kind.extension), kind);
            this.className = className;
            outputStream = new ByteArrayOutputStream();
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return outputStream;
        }

        public byte[] getBytes() {
            return outputStream.toByteArray();
        }

        public String getClassName() {
            return className;
        }
    }

    /**
     * 支持从 ClassLoader 的资源中读取编译需要的类信息
     */
    private static class InputClassJavaFileObject implements JavaFileObject {

        private final String binaryName;
        private final URI uri;

        protected InputClassJavaFileObject(String binaryName, URI uri) {
            this.binaryName = binaryName;
            this.uri = uri;
        }

        public String getBinaryName() {
            return binaryName;
        }

        @Override
        public URI toUri() {
            return uri;
        }

        @Override
        public InputStream openInputStream() throws IOException {
            return uri.toURL().openStream();
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getName() {
            // 操作系统用 uri.getPath()，JAR 用 uri.getSchemeSpecificPart()
            return uri.getPath() == null ? uri.getSchemeSpecificPart() : uri.getPath();
        }

        @Override
        public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Writer openWriter() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLastModified() {
            return 0;
        }

        @Override
        public boolean delete() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Kind getKind() {
            return Kind.CLASS;
        }

        @Override
        public boolean isNameCompatible(String simpleName, Kind kind) {
            String baseName = simpleName + kind.extension;
            String name = getName();
            return kind.equals(getKind()) && (baseName.equals(name) || name.endsWith("/" + baseName));
        }

        @Override
        public NestingKind getNestingKind() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Modifier getAccessLevel() {
            return null;
        }

        @Override
        public String toString() {
            return "InputClassJavaFileObject[uri=" + uri + ", binaryName=" + binaryName + "]";
        }
    }

    private static class InMemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

        private static final String CLASS_FILE_EXTENSION = ".class";

        private final ClassLoader classLoader;
        private final List<OutputClassJavaFileObject> outputFiles;

        protected InMemoryJavaFileManager(ClassLoader loader, JavaFileManager fileManager) {
            super(fileManager);
            classLoader = loader;
            outputFiles = new ArrayList<OutputClassJavaFileObject>();
        }

        // --------------------------------- Output ---------------------------------
        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind,
                                                   FileObject sibling) throws IOException {
            OutputClassJavaFileObject file = new OutputClassJavaFileObject(className, kind);
            outputFiles.add(file);
            return file;
        }

        public List<OutputClassJavaFileObject> getOutputs() {
            return outputFiles;
        }

        @Override
        public String inferBinaryName(Location location, JavaFileObject file) {
            if (file instanceof InputClassJavaFileObject) {
                return ((InputClassJavaFileObject) file).getBinaryName();
            } else {
                return super.inferBinaryName(location, file);
            }
        }

        @Override
        public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse)
                throws IOException {
            if (location == StandardLocation.CLASS_PATH && kinds.contains(Kind.CLASS)) {
                if ("".equals(packageName) || packageName.startsWith("java")) {
                    return super.list(location, packageName, kinds, recurse);
                } else {
                    return find(packageName);
                }
            }
            return super.list(location, packageName, kinds, recurse);
        }

        public List<JavaFileObject> find(String packageName) throws IOException {
            String javaPackageName = packageName.replaceAll("\\.", "/");

            List<JavaFileObject> result = new ArrayList<JavaFileObject>();

            // 从 classLoader 里面查找编译需要的类
            Enumeration<URL> urlEnumeration = classLoader.getResources(javaPackageName);
            while (urlEnumeration.hasMoreElements()) {
                URL packageFolderURL = urlEnumeration.nextElement();
                result.addAll(listUnder(packageName, packageFolderURL));
            }

            return result;
        }

        private Collection<JavaFileObject> listUnder(String packageName, URL packageFolderURL) {
            File directory = new File(packageFolderURL.getFile());
            if (directory.isDirectory()) {
                return processDir(packageName, directory);
            } else {
                return processJar(packageFolderURL);
            }
        }

        private List<JavaFileObject> processJar(URL packageFolderURL) {
            List<JavaFileObject> result = new ArrayList<JavaFileObject>();
            try {
                String externalURL = packageFolderURL.toExternalForm();
                int laste = externalURL.lastIndexOf('!');
                String jarUri = laste <= 0 ? externalURL : externalURL.substring(0, laste);

                JarURLConnection jarConn = (JarURLConnection) packageFolderURL.openConnection();
                String rootEntryName = jarConn.getEntryName();
                if (rootEntryName == null) {
                    return result;
                }
                int rootEnd = rootEntryName.length() + 1;

                Enumeration<JarEntry> entryEnum = jarConn.getJarFile().entries();
                while (entryEnum.hasMoreElements()) {
                    JarEntry jarEntry = entryEnum.nextElement();
                    String name = jarEntry.getName();
                    if (name.startsWith(rootEntryName) && name.indexOf('/', rootEnd) == -1
                            && name.endsWith(CLASS_FILE_EXTENSION)) {
                        URI uri = URI.create(jarUri + "!/" + name);
                        String binaryName = name.replace('/', '.');
                        binaryName = binaryName.replaceAll(CLASS_FILE_EXTENSION + "$", "");

                        result.add(new InputClassJavaFileObject(binaryName, uri));
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Fail to open " + packageFolderURL + " as a jar file", e);
            }
            return result;
        }

        private List<JavaFileObject> processDir(String packageName, File directory) {
            List<JavaFileObject> result = new ArrayList<JavaFileObject>();

            File[] childFiles = directory.listFiles();
            for (File childFile : childFiles) {
                if (childFile.isFile()) {
                    if (childFile.getName().endsWith(CLASS_FILE_EXTENSION)) {
                        String binaryName = packageName + "." + childFile.getName();
                        binaryName = binaryName.replaceAll(CLASS_FILE_EXTENSION + "$", "");

                        result.add(new InputClassJavaFileObject(binaryName, childFile.toURI()));
                    }
                }
            }

            return result;
        }
    }

    /**
     * 支持从 String 中读取源码内容用于编译
     */
    public static class InputStringJavaFileObject extends SimpleJavaFileObject {
        private final String code;

        public InputStringJavaFileObject(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return code;
        }
    }
}
