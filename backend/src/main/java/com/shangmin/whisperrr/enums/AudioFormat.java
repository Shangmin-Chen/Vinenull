package com.shangmin.whisperrr.enums;

/**
 * Enum representing supported audio formats for transcription.
 * 
 * @author shangmin
 * @version 1.0
 */
public enum AudioFormat {
    MP3("audio/mpeg", "mp3"),
    WAV("audio/wav", "wav"),
    M4A("audio/mp4", "m4a"),
    FLAC("audio/flac", "flac"),
    OGG("audio/ogg", "ogg");
    
    private final String mimeType;
    private final String extension;
    
    /**
     * Constructor for AudioFormat enum.
     * 
     * @param mimeType MIME type of the audio format
     * @param extension File extension of the audio format
     */
    AudioFormat(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }
    
    /**
     * Gets the MIME type of the audio format.
     * 
     * @return String MIME type
     */
    public String getMimeType() {
        return mimeType;
    }
    
    /**
     * Gets the file extension of the audio format.
     * 
     * @return String file extension
     */
    public String getExtension() {
        return extension;
    }
    
    /**
     * Gets the AudioFormat enum from a file extension.
     * 
     * @param extension File extension (with or without dot)
     * @return AudioFormat enum or null if not found
     */
    public static AudioFormat fromExtension(String extension) {
        if (extension == null) {
            return null;
        }
        
        // Remove dot if present
        String cleanExtension = extension.startsWith(".") ? extension.substring(1) : extension;
        
        for (AudioFormat format : values()) {
            if (format.extension.equalsIgnoreCase(cleanExtension)) {
                return format;
            }
        }
        return null;
    }
    
    /**
     * Gets the AudioFormat enum from a MIME type.
     * 
     * @param mimeType MIME type string
     * @return AudioFormat enum or null if not found
     */
    public static AudioFormat fromMimeType(String mimeType) {
        if (mimeType == null) {
            return null;
        }
        
        for (AudioFormat format : values()) {
            if (format.mimeType.equalsIgnoreCase(mimeType)) {
                return format;
            }
        }
        return null;
    }
    
    /**
     * Checks if the given extension is supported.
     * 
     * @param extension File extension to check
     * @return true if supported, false otherwise
     */
    public static boolean isSupported(String extension) {
        return fromExtension(extension) != null;
    }
}
