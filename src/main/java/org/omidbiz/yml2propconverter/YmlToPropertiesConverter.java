package org.omidbiz.yml2propconverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;

/**
 * @author omidp
 *
 */
public class YmlToPropertiesConverter implements Converter {

	static ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(Feature.WRITE_DOC_START_MARKER));

	static {
		mapper.findAndRegisterModules();
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	@Override
	public void convert(InputStream ymlFileContent, OutputStream os) throws JsonProcessingException, IOException {
		JsonNode readTree = mapper.readTree(ymlFileContent);
		List<RootNode> read = fetchRoot(readTree);
		fetchChildren(readTree, read);
		for (RootNode rootNode : read) {
			StringBuilder sb = new StringBuilder();
			sb.append(rootNode.getName());
			geenrateContent(rootNode, sb, os);
		}
	}

	private void geenrateContent(RootNode rootNode, StringBuilder sb, OutputStream os)
			throws UnsupportedEncodingException, IOException {
		if (JsonNodeType.OBJECT.equals(rootNode.getNodeType()) == false) {
			String line = sb.toString() + "=" + rootNode.getValue() + "\r\n";
			os.write(line.getBytes("UTF-8"));
			sb = new StringBuilder();
		}
		List<RootNode> children = rootNode.getChildren();
		for (RootNode rn : children) {
			StringBuilder nodeNames = new StringBuilder();
			nodeNames.append(".").append(rn.getName());
			geenrateContent(rn, new StringBuilder(sb).append(nodeNames), os);
		}
	}

	private void fetchChildren(JsonNode readTree, List<RootNode> read) throws IOException {
		for (RootNode key : read) {
			List<RootNode> nodes = new ArrayList<>();
			JsonNode path = readTree.path(key.getName());
			Iterator<Entry<String, JsonNode>> fieldNames = path.fields();

			while (fieldNames.hasNext()) {
				Map.Entry<java.lang.String, com.fasterxml.jackson.databind.JsonNode> entry = (Map.Entry<java.lang.String, com.fasterxml.jackson.databind.JsonNode>) fieldNames
						.next();
				RootNode rn = new RootNode(entry.getKey(), entry.getValue().getNodeType());
				rn.setValue(path.findValue(entry.getKey()));
				rn.setParnet(key);
				nodes.add(rn);

			}

			fetchChildren(path, nodes);

		}
	}

	public List<RootNode> fetchRoot(JsonNode rootNode) throws IOException {
		Iterator<Entry<String, JsonNode>> fieldNames = rootNode.fields();
		List<RootNode> res = new ArrayList<>();
		while (fieldNames.hasNext()) {
			Map.Entry<java.lang.String, com.fasterxml.jackson.databind.JsonNode> entry = (Map.Entry<java.lang.String, com.fasterxml.jackson.databind.JsonNode>) fieldNames
					.next();
			RootNode rn = new RootNode(entry.getKey(), entry.getValue().getNodeType());
			rn.setValue(rootNode.findValue(entry.getKey()));
			res.add(rn);
		}
		return res;

	}

	public static class RootNode {
		private List<RootNode> children = new ArrayList<>();
		private String name;
		private JsonNodeType nodeType;
		private Object value;

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public RootNode(String name, JsonNodeType nodeType) {
			this.name = name;
			this.nodeType = nodeType;
		}

		public void addChildren(RootNode child) {
			children.add(child);
		}

		public List<RootNode> getChildren() {
			return children;
		}

		public JsonNodeType getNodeType() {
			return nodeType;
		}

		@Override
		public String toString() {
			return name + " : " + nodeType + " : " + value;
		}

		public void setParnet(RootNode parnet) {
			if (parnet != null)
				parnet.addChildren(this);
		}

	}

}
