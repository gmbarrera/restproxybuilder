package com.gbarrera.tap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;


public class RestBuilder {

	private Configuration configuration;
	private static Pattern classDeclarationPattern = Pattern.compile(".*class ([^\\s]*) ");
	private static Pattern packagePattern = Pattern.compile("^\\s*package\\s*([^\\s]*)\\s*;");
	private ServerClass serverClass;
	
	private void processClass(Path javaFile) {

		System.out.println("\t" + javaFile);

		String packageName = "";
		ArrayList<String> classesName = new ArrayList<>();

		try {
			BufferedReader bufReader = new BufferedReader(new FileReader(javaFile.toFile()));
			String currentLine;

			while ((currentLine = bufReader.readLine()) != null) {

				Matcher packageMatcher = packagePattern.matcher(currentLine);
				if (packageMatcher.find()) {
					packageName = packageMatcher.group(1);
				}

				Matcher classDeclarationMatcher = classDeclarationPattern.matcher(currentLine);
				if (classDeclarationMatcher.find()) {
					classesName.add(classDeclarationMatcher.group(1));
				}
			}

			bufReader.close();

			
			URL url = new URL("file:///" + configuration.getTempPath());
			URL[] urls = new URL[] { url };

			URLClassLoader cl = new URLClassLoader(urls);
			for (String className : classesName) {
				
				Class<?> clazz = cl.loadClass((packageName.isEmpty() ? "" : packageName + ".") + className);

				System.out.println("\t\t" + clazz.getName());
				
				for(Method method : clazz.getDeclaredMethods()) {
						
					PublishMethod publishMethodAnnotation = (PublishMethod)method.getDeclaredAnnotation(PublishMethod.class);
					
					if (publishMethodAnnotation != null) {
						System.out.print("\t\t\tMethod: " + method.getName());						
						System.out.println("\tType: " + publishMethodAnnotation.type());
						
						serverClass.getMethods().add(method);
						
						
						
						
						
						
						
					}
				}
				
				System.out.println();
			}
			cl.close();
			
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public boolean generateClasses(ArrayList<File> javaFiles) {
		boolean success = false;
	
		try {
			String[] compileOptions = new String[] { "-d", configuration.getTempPath() };
			
			Iterable<String> compilationOptions = Arrays.asList(compileOptions);
	
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
			
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
			
			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(javaFiles);
			JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, compilationOptions,
					null, compilationUnits);
			success = task.call();
			
			fileManager.close();
	
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return success;
	}
	
	private void processLogic() throws Exception {

		System.out.println("\nProcesando la logica de negocio:");
		System.out.println("Procesando...");
		
		serverClass = new ServerClass(configuration.getServerClassName());
		ArrayList<File> javaFiles = new ArrayList<>();
		
		Files.walk(Paths.get(configuration.getLogicPath()))
		        .filter(f -> f.toFile().getName().endsWith(".java"))
				.forEach(f ->  javaFiles.add(f.toFile()));
		
		if (!generateClasses(javaFiles))
			throw new Exception("Compilation Error.");
		
		for(File file : javaFiles) 
			processClass(file.toPath());
		
		
		
		System.out.println(serverClass.buildJavaCode());

		
	}
	
	
	private void loadConfiguration() {

		this.configuration = new Configuration();

		try {
			configuration.load();
			configuration.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void create() {
		loadConfiguration();

		// Load logic java
		try {
			
			processLogic();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void update() {
		loadConfiguration();

	}

	public static void main(String[] args) {

		if (args.length < 1) {

			System.out.println("Error. Falta parametros.");
			System.out.println("Uso: java RestBuilder create|update");

			System.exit(1);
		}

		String command = args[0];
		RestBuilder rb = new RestBuilder();

		switch (command) {
		case "create":
			rb.create();
			break;

		case "update":
			rb.update();
			break;

		default:
			System.err.println("Error. Parámetros incorrecto.");
			System.err.println("Uso: java RestBuilder create|update");
			System.exit(1);
		}
	}
}
