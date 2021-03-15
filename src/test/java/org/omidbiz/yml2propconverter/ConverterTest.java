package org.omidbiz.yml2propconverter;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author omidp
 *
 */
public class ConverterTest {

	@Test
	@Ignore
	public void toProp() throws JsonProcessingException, IOException
	{
		Converter ymlToPropertiesConverter = new YmlToPropertiesConverter();
		ymlToPropertiesConverter.convert(ConverterTest.class.getResourceAsStream("/application.yml"), System.out);
	}
	
	
	@Test
//	@Ignore
	public void toYml() throws JsonProcessingException, IOException
	{
		Converter ymlToPropertiesConverter = new PropertiesToYmlConverter();
		ymlToPropertiesConverter.convert(ConverterTest.class.getResourceAsStream("/application.properties"), System.out);
	}
	
}
