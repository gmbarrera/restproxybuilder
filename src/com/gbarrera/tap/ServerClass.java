package com.gbarrera.tap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;


public class ServerClass {
	private String serverClassName;
	private ArrayList<Method> methods;
	
	private HashMap<String, Class<?>> logicClasses;
	
	public String getServerClassName() {
		return serverClassName;
	}

	public ArrayList<Method> getMethods() {
		return methods;
	}
	
	public HashMap<String, Class<?>> getLogicClasses() {
		return logicClasses;
	}
	
	
	public ServerClass(String serverClassName) {
		this.serverClassName = serverClassName;
		methods = new ArrayList<>();
		logicClasses = new HashMap<>();
	}
	
	public String buildJavaCode() {
		String code = ""; 
		
		code += "import javax.ws.rs.Consumes;\n";
		code += "import javax.ws.rs.GET;\n";
		code += "import javax.ws.rs.POST;\n";
		code += "import javax.ws.rs.Path;\n";
		code += "import javax.ws.rs.PathParam;\n";
		code += "import javax.ws.rs.Produces;\n";
		code += "import javax.ws.rs.core.MediaType;\n";
		code += "import javax.ws.rs.core.Response;\n";
		code += "\n";
		code += "public class " + this.serverClassName + " {\n";
		code += "\n";
		
		for(Class<?> logicClass : this.logicClasses.values()) {
			code += "    " + logicClass.getName() + " obj" + logicClass.getSimpleName() + " = new " + logicClass.getName() + "()\n";
		}
		
		for(Method method : methods) {

			code += "\n";
			
			PublishMethod publishMethodAnnotation = (PublishMethod)method.getDeclaredAnnotation(PublishMethod.class);
			
			switch(publishMethodAnnotation.type()){
			case GET:
				code += buildGetMethod(method);
				break;
				
			case POST:
				code += buildPostMethod(method);
				break;
				
			case DELETE:
				code += buildDeleteMethod(method);
				break;
				
			case PUT:
				code += buildPutMethod(method);
				break;
			}
		}
		
		code += "}\n";
		
		return code;
	}

	private String buildGetMethod(Method method) {
		String methodCode = "";
		int paramCount = 0;
		
		methodCode += "    @GET\n";
		methodCode += "    @Path(\"/{id}\")\n";
		methodCode += "    @Produces(MediaType.APPLICATION_JSON)\n";
		
		methodCode += "    public " + method.getReturnType().getTypeName() + " " +  method.getName() + "(";
		
		for(Class<?> param : method.getParameterTypes()) {
			methodCode += param.getTypeName() + " param" + paramCount++ + ", "; 
		}
		if (method.getParameterTypes().length > 0)
			methodCode = methodCode.substring(0, methodCode.length() - 2);
	
		methodCode += ") {\n";
		
		methodCode += "        return bl.getCustomer(id);";
		
		
		methodCode += "    }\n"; 
		
		return methodCode;
	}
	
	private String buildPostMethod(Method method) {
		String methodCode = "";
		
		methodCode += "    @POST\n";
		methodCode += "    @Path(\"/{id}\")\n";
		methodCode += "    @Consume(MediaType.APPLICATION_JSON)\n";
		methodCode += "    public " + method.getReturnType().getTypeName() + " " +  method.getName() + "() {\n";
		
		
		
		
		methodCode += "    }\n"; 
		
		return methodCode;
	}

	private String buildDeleteMethod(Method method) {
		// TODO Auto-generated method stub
		return null;
	}

	private String buildPutMethod(Method method) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
