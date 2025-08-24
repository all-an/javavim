package com.javavim.search;

import com.javavim.buffer.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Handles search and replace operations in text buffers.
 * Follows single responsibility principle - manages search operations only.
 */
public class SearchEngine {
    
    public List<SearchResult> findAll(Buffer buffer, String searchText, boolean caseSensitive) {
        if (buffer == null || searchText == null || searchText.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<SearchResult> results = new ArrayList<>();
        
        for (int lineNumber = 0; lineNumber < buffer.getLineCount(); lineNumber++) {
            String line = buffer.getLine(lineNumber);
            findInLine(line, lineNumber, searchText, caseSensitive, results);
        }
        
        return results;
    }
    
    public List<SearchResult> findAllRegex(Buffer buffer, String regexPattern, boolean caseSensitive) {
        if (buffer == null || regexPattern == null || regexPattern.isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            Pattern pattern = createPattern(regexPattern, caseSensitive);
            return findWithPattern(buffer, pattern);
        } catch (PatternSyntaxException e) {
            return new ArrayList<>();
        }
    }
    
    public SearchResult findNext(Buffer buffer, String searchText, int startLine, int startPosition, boolean caseSensitive) {
        if (buffer == null || searchText == null || searchText.isEmpty()) {
            return null;
        }
        
        return findFromPosition(buffer, searchText, startLine, startPosition, caseSensitive, true);
    }
    
    public SearchResult findPrevious(Buffer buffer, String searchText, int startLine, int startPosition, boolean caseSensitive) {
        if (buffer == null || searchText == null || searchText.isEmpty()) {
            return null;
        }
        
        return findFromPosition(buffer, searchText, startLine, startPosition, caseSensitive, false);
    }
    
    public int replaceAll(Buffer buffer, String searchText, String replaceText, boolean caseSensitive) {
        if (buffer == null || searchText == null || replaceText == null) {
            return 0;
        }
        
        List<SearchResult> results = findAll(buffer, searchText, caseSensitive);
        return performReplacements(buffer, results, replaceText);
    }
    
    public int replaceAllRegex(Buffer buffer, String regexPattern, String replaceText, boolean caseSensitive) {
        if (buffer == null || regexPattern == null || replaceText == null) {
            return 0;
        }
        
        List<SearchResult> results = findAllRegex(buffer, regexPattern, caseSensitive);
        return performReplacements(buffer, results, replaceText);
    }
    
    private void findInLine(String line, int lineNumber, String searchText, boolean caseSensitive, List<SearchResult> results) {
        String searchLine = caseSensitive ? line : line.toLowerCase();
        String searchTarget = caseSensitive ? searchText : searchText.toLowerCase();
        
        int position = 0;
        while (position < searchLine.length()) {
            int foundIndex = searchLine.indexOf(searchTarget, position);
            if (foundIndex == -1) {
                break;
            }
            
            results.add(new SearchResult(lineNumber, foundIndex, searchText.length(), searchText));
            position = foundIndex + 1;
        }
    }
    
    private List<SearchResult> findWithPattern(Buffer buffer, Pattern pattern) {
        List<SearchResult> results = new ArrayList<>();
        
        for (int lineNumber = 0; lineNumber < buffer.getLineCount(); lineNumber++) {
            String line = buffer.getLine(lineNumber);
            var matcher = pattern.matcher(line);
            
            while (matcher.find()) {
                results.add(new SearchResult(
                    lineNumber, 
                    matcher.start(), 
                    matcher.end() - matcher.start(),
                    matcher.group()
                ));
            }
        }
        
        return results;
    }
    
    private SearchResult findFromPosition(Buffer buffer, String searchText, int startLine, int startPosition, boolean caseSensitive, boolean forward) {
        if (forward) {
            return findForward(buffer, searchText, startLine, startPosition, caseSensitive);
        } else {
            return findBackward(buffer, searchText, startLine, startPosition, caseSensitive);
        }
    }
    
    private SearchResult findForward(Buffer buffer, String searchText, int startLine, int startPosition, boolean caseSensitive) {
        for (int lineNumber = startLine; lineNumber < buffer.getLineCount(); lineNumber++) {
            String line = buffer.getLine(lineNumber);
            String searchLine = caseSensitive ? line : line.toLowerCase();
            String searchTarget = caseSensitive ? searchText : searchText.toLowerCase();
            
            int searchStart = (lineNumber == startLine) ? startPosition : 0;
            int foundIndex = searchLine.indexOf(searchTarget, searchStart);
            
            if (foundIndex != -1) {
                return new SearchResult(lineNumber, foundIndex, searchText.length(), searchText);
            }
        }
        
        return null;
    }
    
    private SearchResult findBackward(Buffer buffer, String searchText, int startLine, int startPosition, boolean caseSensitive) {
        for (int lineNumber = startLine; lineNumber >= 0; lineNumber--) {
            String line = buffer.getLine(lineNumber);
            String searchLine = caseSensitive ? line : line.toLowerCase();
            String searchTarget = caseSensitive ? searchText : searchText.toLowerCase();
            
            int searchEnd = (lineNumber == startLine) ? startPosition : line.length();
            int foundIndex = searchLine.lastIndexOf(searchTarget, searchEnd - searchText.length());
            
            if (foundIndex != -1) {
                return new SearchResult(lineNumber, foundIndex, searchText.length(), searchText);
            }
        }
        
        return null;
    }
    
    private int performReplacements(Buffer buffer, List<SearchResult> results, String replaceText) {
        // Replace from end to beginning to maintain correct positions
        for (int i = results.size() - 1; i >= 0; i--) {
            SearchResult result = results.get(i);
            String line = buffer.getLine(result.getLineNumber());
            
            String newLine = line.substring(0, result.getStartPosition()) + 
                           replaceText + 
                           line.substring(result.getStartPosition() + result.getLength());
            
            buffer.setLine(result.getLineNumber(), newLine);
        }
        
        return results.size();
    }
    
    private Pattern createPattern(String regexPattern, boolean caseSensitive) {
        int flags = caseSensitive ? 0 : Pattern.CASE_INSENSITIVE;
        return Pattern.compile(regexPattern, flags);
    }
    
    public static class SearchResult {
        private final int lineNumber;
        private final int startPosition;
        private final int length;
        private final String matchedText;
        
        public SearchResult(int lineNumber, int startPosition, int length, String matchedText) {
            this.lineNumber = lineNumber;
            this.startPosition = startPosition;
            this.length = length;
            this.matchedText = matchedText;
        }
        
        public int getLineNumber() {
            return lineNumber;
        }
        
        public int getStartPosition() {
            return startPosition;
        }
        
        public int getLength() {
            return length;
        }
        
        public String getMatchedText() {
            return matchedText;
        }
        
        public int getEndPosition() {
            return startPosition + length;
        }
    }
}