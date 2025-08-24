package com.javavim.cli;

/**
 * Parses command line arguments for JavaVim editor.
 * Follows code guidance: returns values instead of void, single responsibility.
 */
public class CommandLineParser {
    
    public CommandLineResult parseArguments(String[] args) {
        if (args == null) {
            return CommandLineResult.noArgs();
        }
        
        return processArguments(args);
    }
    
    private CommandLineResult processArguments(String[] args) {
        if (args.length == 0) {
            return CommandLineResult.noArgs();
        }
        
        return processFirstArgument(args);
    }
    
    private CommandLineResult processFirstArgument(String[] args) {
        String firstArg = args[0];
        
        if (isHelpRequest(firstArg)) {
            return CommandLineResult.help();
        }
        
        return CommandLineResult.fileArgument(firstArg);
    }
    
    private boolean isHelpRequest(String arg) {
        return "--help".equals(arg) || "-h".equals(arg) || "help".equals(arg);
    }
    
    public static class CommandLineResult {
        private final boolean hasFile;
        private final boolean isHelp;
        private final boolean isEmpty;
        private final String filename;
        private final String helpMessage;
        
        private CommandLineResult(boolean hasFile, boolean isHelp, boolean isEmpty, 
                                String filename, String helpMessage) {
            this.hasFile = hasFile;
            this.isHelp = isHelp;
            this.isEmpty = isEmpty;
            this.filename = filename;
            this.helpMessage = helpMessage;
        }
        
        public boolean hasFile() {
            return hasFile;
        }
        
        public boolean isHelp() {
            return isHelp;
        }
        
        public boolean isEmpty() {
            return isEmpty;
        }
        
        public String getFilename() {
            return filename;
        }
        
        public String getHelpMessage() {
            return helpMessage;
        }
        
        static CommandLineResult fileArgument(String filename) {
            return new CommandLineResult(true, false, false, filename, null);
        }
        
        static CommandLineResult help() {
            String message = buildHelpMessage();
            return new CommandLineResult(false, true, false, null, message);
        }
        
        static CommandLineResult noArgs() {
            return new CommandLineResult(false, false, true, null, null);
        }
        
        private static String buildHelpMessage() {
            StringBuilder help = new StringBuilder();
            help.append("JavaVim - Terminal Vim Editor v1.0.0\n");
            help.append("\n");
            help.append("USAGE:\n");
            help.append("  javavim [filename]        Open or create a file\n");
            help.append("  javavim                   Start with welcome screen\n");
            help.append("  javavim --help            Show this help message\n");
            help.append("\n");
            help.append("EXAMPLES:\n");
            help.append("  javavim Hello.java        Edit Hello.java\n");
            help.append("  javavim /path/to/file.txt Edit file with full path\n");
            help.append("\n");
            help.append("VIM COMMANDS:\n");
            help.append("  i                         Enter insert mode\n");
            help.append("  ESC                       Return to normal mode\n");
            help.append("  :w                        Save file\n");
            help.append("  :q                        Quit editor\n");
            help.append("  :wq                       Save and quit\n");
            help.append("  h, j, k, l                Move cursor left, down, up, right\n");
            return help.toString();
        }
    }
}