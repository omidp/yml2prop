package org.omidbiz.yml2propconverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author omidp
 *
 */
public class PropertiesToYmlConverter implements Converter {

	@Override
	public void convert(InputStream propFileContent, OutputStream os) throws JsonProcessingException, IOException {
		Properties prop = new Properties();
		prop.load(propFileContent);
		Iterator<Entry<Object, Object>> entrySet = prop.entrySet().iterator();
		List<Data> propLines = new ArrayList<>();
		while (entrySet.hasNext()) {
			Map.Entry<java.lang.Object, java.lang.Object> entry = (Map.Entry<java.lang.Object, java.lang.Object>) entrySet
					.next();
			String key = (String) entry.getKey();
			propLines.add(new Data(key, entry.getValue()));
		}

		Map<Node, List<Node>> nodeList = new HashMap<>();
		for (Data lineKeyValue : propLines) {
			parse(lineKeyValue, nodeList, lineKeyValue.getValue());
		}
//
		for (Map.Entry<Node, List<Node>> entry : nodeList.entrySet()) {
			Node key = entry.getKey();
			generate(key, new StringBuilder().append("\r").append(key.getName()).append(":"), System.out);
		}
	}

	private void generate(Node root, StringBuilder sb, OutputStream os)
			throws UnsupportedEncodingException, IOException {
		Set<Node> children = root.getChildren();
		os.write(sb.toString().getBytes("UTF-8"));
		for (Node node : children) {
			StringBuilder nl = new StringBuilder();
			nl.append("\r").append(indent(node.getIndentation())).append(node.getName()).append(":");
			if (node.getValue() != null)
				nl.append(indent(1)).append(node.getValue());
			generate(node, nl, os);
		}
	}

	public Node[] shiftLeft(Node[] nums) {
		if (nums == null || nums.length <= 1) {
			return nums;
		}
		Node[] narr = new Node[nums.length - 1];
		System.arraycopy(nums, 1, narr, 0, narr.length);
		return narr;
	}

	private String indent(int cnt) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cnt; i++) {
			sb.append("  ");
		}
		return sb.toString();
	}

	private void parse(Data dataLine, Map<Node, List<Node>> nodeList, Object object) {
		String key = dataLine.getKey();
		if (key.indexOf(".") > 0) {
			String[] split = key.split("\\.");
			List<Node> nodes = new ArrayList<>();
			Node rootNode = null;
			for (int i = 0; i < split.length; i++) {
				String name = split[i];
				Node node = new Node(name);
				if (i == 0) {
					rootNode = node;
					List<Node> list = nodeList.get(rootNode);
					Node traverse = traverse(node, list, name);
					if (traverse != null)
						node = traverse;
					nodes.add(rootNode);
				} else {
					List<Node> list = nodeList.get(rootNode);
					Node traverse = traverse(node, list, name);
					if (traverse != null)
						node = traverse;
					Node parentNode = nodes.get(i - 1);
					Node traverseParent = traverse(parentNode, list, parentNode.getName());
					if (traverseParent != null)
						parentNode = traverseParent;
					node.setParnet(parentNode);
					node.setIndentation(i);
					nodes.add(node);
					if (i == (split.length - 1))
						node.setValue(object);

				}
			}

			nodeList.put(rootNode, nodes);
		}

	}

	private Node traverse(Node rootNode, List<Node> list, String name) {
		if (list == null) {
			return null;
		}
		Optional<Node> findFirst = list.stream().filter(f -> f.getName().equals(name)).findFirst();
		if (findFirst.isPresent()) {
			return findFirst.get();
		}
		return null;
	}

	public static class Data implements Comparable<Data> {
		private String key;
		private Object value;

		public Data(String key, Object value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public Object getValue() {
			return value;
		}

		@Override
		public int compareTo(Data o) {
			return o.getKey().compareTo(getKey());
		}

		@Override
		public String toString() {
			return "key : " + this.key + " value :" + this.value;
		}

	}

	public static class Node {

		private String name;

		public Node parent;

		private Object value;

		private int indentation;

		private Set<Node> children = new HashSet<>();

		public int getIndentation() {
			return indentation;
		}

		public void setIndentation(int indentation) {
			this.indentation = indentation;
		}

		public Node(String name) {
			this.name = name;
		}

		public void addChildren(Node child) {
			if (child != null)
				children.add(child);
		}

		public Set<Node> getChildren() {
			return children;
		}

		public String getName() {
			return name;
		}

		public void setParnet(Node parnet) {
			if (parnet != null)
				parnet.addChildren(this);
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

	}

}
