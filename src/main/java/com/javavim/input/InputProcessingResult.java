package com.javavim.input;

import com.javavim.Javavim;

/**
 * Represents the result of processing an input character.
 * Follows code guidance: returns meaningful values instead of void.
 */
public class InputProcessingResult {
    
    private final boolean processed;
    private final boolean hasError;
    private final boolean shouldQuit;
    private final boolean hasModeChange;
    private final boolean hasCommandExecution;
    private final boolean hasCursorMovement;
    private final boolean hasTextInsertion;
    private final boolean hasWarning;
    private final String message;
    private final Javavim.EditorMode newMode;
    private final String executedCommand;
    private final String movementDirection;
    private final char insertedCharacter;

    private InputProcessingResult(Builder builder) {
        this.processed = builder.processed;
        this.hasError = builder.hasError;
        this.shouldQuit = builder.shouldQuit;
        this.hasModeChange = builder.hasModeChange;
        this.hasCommandExecution = builder.hasCommandExecution;
        this.hasCursorMovement = builder.hasCursorMovement;
        this.hasTextInsertion = builder.hasTextInsertion;
        this.hasWarning = builder.hasWarning;
        this.message = builder.message;
        this.newMode = builder.newMode;
        this.executedCommand = builder.executedCommand;
        this.movementDirection = builder.movementDirection;
        this.insertedCharacter = builder.insertedCharacter;
    }

    public boolean wasProcessed() {
        return processed;
    }

    public boolean hasError() {
        return hasError;
    }

    public boolean shouldQuitEditor() {
        return shouldQuit;
    }

    public boolean hasModeChange() {
        return hasModeChange;
    }

    public boolean hasCommandExecution() {
        return hasCommandExecution;
    }

    public boolean hasCursorMovement() {
        return hasCursorMovement;
    }

    public boolean hasTextInsertion() {
        return hasTextInsertion;
    }

    public boolean hasWarning() {
        return hasWarning;
    }

    public String getMessage() {
        return message;
    }

    public Javavim.EditorMode getNewMode() {
        return newMode;
    }

    public String getExecutedCommand() {
        return executedCommand;
    }

    public String getMovementDirection() {
        return movementDirection;
    }

    public char getInsertedCharacter() {
        return insertedCharacter;
    }

    public static Builder success(String message) {
        return new Builder().processed(true).message(message);
    }

    public static Builder error(String errorMessage) {
        return new Builder().processed(false).hasError(true).message(errorMessage);
    }

    public static Builder ignored(String reason) {
        return new Builder().processed(false).message(reason);
    }

    public static class Builder {
        private boolean processed = false;
        private boolean hasError = false;
        private boolean shouldQuit = false;
        private boolean hasModeChange = false;
        private boolean hasCommandExecution = false;
        private boolean hasCursorMovement = false;
        private boolean hasTextInsertion = false;
        private boolean hasWarning = false;
        private String message = "";
        private Javavim.EditorMode newMode;
        private String executedCommand;
        private String movementDirection;
        private char insertedCharacter;

        public Builder processed(boolean processed) {
            this.processed = processed;
            return this;
        }

        public Builder hasError(boolean hasError) {
            this.hasError = hasError;
            return this;
        }

        public Builder shouldQuit(boolean shouldQuit) {
            this.shouldQuit = shouldQuit;
            return this;
        }

        public Builder modeChange(Javavim.EditorMode newMode) {
            this.hasModeChange = true;
            this.newMode = newMode;
            return this;
        }

        public Builder commandExecution(String command) {
            this.hasCommandExecution = true;
            this.executedCommand = command;
            return this;
        }

        public Builder cursorMovement(String direction) {
            this.hasCursorMovement = true;
            this.movementDirection = direction;
            return this;
        }

        public Builder textInsertion(char character) {
            this.hasTextInsertion = true;
            this.insertedCharacter = character;
            return this;
        }

        public Builder warning(boolean hasWarning) {
            this.hasWarning = hasWarning;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public InputProcessingResult build() {
            return new InputProcessingResult(this);
        }
    }
}