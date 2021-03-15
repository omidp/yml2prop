package org.omidbiz.yml2propconverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author omidp
 *
 */
public interface Converter {

	public void convert(InputStream fileContent, OutputStream os) throws JsonProcessingException, IOException;
	
}
