package kr.co.swiftER.autocomplete;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
	
	// Trie는 단어들을 저장할 수 있는 트리 구조로, 각 노드는 단어의 각 글자 하나씩 저장
	// Character는 하나의 char값을 가지는 public final 자바 클래스; 제네릭에 char은 사용 불가하지만 Character은 가능
	
	private final Map<Character, TrieNode> children; // 해당 노드가 저장할 글자와 이후 이어질 노드의 정보를 저장하는 객체 
	private boolean isEndOfWord;
	
	public TrieNode() {
		children = new HashMap<>();
		isEndOfWord = false;
	}
	
	public Map<Character, TrieNode> getChildren(){
		return children;
	}
	
	public boolean isEndOfWord() {
		return isEndOfWord;
	}
	
	public void setEndOfWord(boolean endOfWord) {
		isEndOfWord = endOfWord;
	}
}
