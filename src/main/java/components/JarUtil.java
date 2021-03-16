package components;

import config.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public abstract class JarUtil {
    private static boolean keystoreReady = false;

    public static Object loadVerifiedJar(String jarPath) throws IOException, InterruptedException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        if (verifyJar(jarPath)) {
            Manifest manifest = new JarFile(new File(jarPath), false).getManifest();
            String mainClass = manifest.getMainAttributes().getValue("Main-Class");

            URL[] urls = {new File(jarPath).toURI().toURL()};
            URLClassLoader urlClassLoader = new URLClassLoader(urls, JarUtil.class.getClassLoader());
            Class<?> clazz = Class.forName(mainClass, true, urlClassLoader);
            Object instance = clazz.getMethod("getInstance").invoke(clazz);
            return clazz.getDeclaredField("port").get(instance);
        }

        throw new RuntimeException("Jar could not be verified");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static String unpackKeystore() throws IOException {
        File signatureFile = new File("signature.jks");

        if (!keystoreReady) {
            if (signatureFile.exists())
                signatureFile.delete();
            signatureFile.createNewFile();
            signatureFile.deleteOnExit();

            Objects.requireNonNull(JarUtil.class.getClassLoader().getResourceAsStream("keystore.jks")).
                    transferTo(new FileOutputStream(signatureFile));

            keystoreReady = true;
        }

        return signatureFile.getAbsolutePath();
    }

    private static boolean verifyJar(String jarPath) throws IOException, InterruptedException {
        File jarVerification = File.createTempFile("jarVerification", "temp.txt");

        String jarsigner = Configuration.instance.jarsigner;
        String keystore = unpackKeystore();
        String storepass = Configuration.instance.keystorePass;

        Process verification = new ProcessBuilder()
                .command(jarsigner, "-verify", "-keystore", keystore, "-storepass", storepass, jarPath)
                .redirectOutput(jarVerification)
                .start();
        verification.waitFor();

        String verificationOutput = new String(new FileInputStream(jarVerification).readAllBytes());
        return verificationOutput.contains("jar verified");
    }
}
