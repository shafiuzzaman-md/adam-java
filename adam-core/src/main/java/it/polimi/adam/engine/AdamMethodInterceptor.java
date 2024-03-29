package it.polimi.adam.engine;

import it.polimi.adam.annotation.Model;
import it.polimi.adam.domain.EmbeddedModel;
import it.polimi.adam.engine.util.Reflection;
import it.polimi.adam.model.EmbeddedModelParser;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class AdamMethodInterceptor implements MethodInterceptor {

	public Object intercept(Object obj, 
							Method method, 
							Object[] args,
							MethodProxy proxy) throws Throwable {
		if(method.isAnnotationPresent(Model.class)){
			
			Model model = method.getAnnotation(Model.class);
			String value = model.value();
			System.out.println("Instantiate and run model "+value);
			
			List<String> paramNames = Reflection.getParamNames(method);
			
			Map<String, Object> executionState = new HashMap<String, Object>();
			for (int i = 0; i < args.length; i++) {
				Object object = args[i];
				executionState.put(paramNames.get(i), object);
			}
			
			EmbeddedModelParser parser = new EmbeddedModelParser();
			EmbeddedModel embeddedModel = parser.parse(getClass().getResourceAsStream("/"+value+".adam"));
			embeddedModel.run(executionState, new UncertaintyManagerImpl(embeddedModel));
			
			if(!method.getReturnType().equals(Void.TYPE)){
				String returnValueName = Reflection.getReturnValueName(method);
				if(returnValueName != null){
					return executionState.get(returnValueName);	
				}
			}
			
			return null;
		}
		else{
			return proxy.invokeSuper(obj, args);	
		}
		
	}

}
