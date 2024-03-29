package it.polimi.adam.engine;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

public class EngineFactory{
	
	public Object createInstance(Class superClass){

		Enhancer e = new Enhancer();
		e.setSuperclass(superClass);
		e.setCallbacks(new Callback[] { new AdamMethodInterceptor() });
		
		Object bean = e.create();
		return bean;
		
	}

}
