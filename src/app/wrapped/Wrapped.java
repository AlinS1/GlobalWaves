package app.wrapped;

import app.user.User;

import java.nio.file.attribute.UserDefinedFileAttributeView;

public interface Wrapped {
    public String getUserType();
    public boolean verifyWrapped();
}

