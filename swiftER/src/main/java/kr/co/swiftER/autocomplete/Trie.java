package kr.co.swiftER.autocomplete;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype") // admin과 message에서 Trie 클래스 inject할 때 각각 다른 instance로 만들기 위해서 필요
public class Trie {
	/**
	 * 검색 시 autocomplete를 구현하기 위한 trie tree 구조의 클래스
	 * 각 단어를 trie tree에 저장해서 검색 키워드와 일치하는 단어들만 tree에서 추출하여 출력하기
	 */
	private final TrieNode root;
	
	public Trie() {
		root = new TrieNode();
	}
	
	// trie tree에 단어 insert
	public void insert(String word) {
		TrieNode node = root;
		for(int i =0; i< word.length(); i++) {
			char ch = word.charAt(i);
			TrieNode child = node.getChildren().get(ch);
			
			if(child == null) { // 기존에 생성된 노드가 없을 시 새로 만든다
				child = new TrieNode();
				node.getChildren().put(ch, child);
			}
			
			node = child;
		}
		
		node.setEndOfWord(true);
	}
	
	// trie tree에서 추출한 단어로 autocomplete하기
	public List<String> autoComplete(String prefix){
		List<String> suggestions = new ArrayList<>();
		TrieNode node = root;
		for(int i=0; i < prefix.length(); i++) {
			char ch = prefix.charAt(i);
			TrieNode child = node.getChildren().get(ch);
			
			if(child == null) { // 일치하는 단어가 없으므로 빈 리스트 리턴
				return suggestions;
			}
			
			node = child;
		}
		
		autoCompleteHelper(prefix, node, suggestions);
		return suggestions;
	}
	
	// autocomplete를 위해 검색 쿼리의 한 글자 씩, tree의 root부터 노드 하나씩 검색하기; recursion 사용
	private void autoCompleteHelper(String prefix, TrieNode node, List<String> suggestions) { // Trie 클래스 내부에서만 사용할 것이므로 private 지시자 사용
		if(node.isEndOfWord()) {
			suggestions.add(prefix);
		}
		
		for(Map.Entry<Character, TrieNode> entry : node.getChildren().entrySet()) {
			char ch = entry.getKey();
			autoCompleteHelper(prefix + ch, entry.getValue(), suggestions);
		}
	}
}
