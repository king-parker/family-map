package model;

import java.util.Random;

/**
 * This abstract class serves as the foundation of each model object. It gives an interface that makes it so
 * each object has the getUsername() and updateID() methods as those are shared between all model objects.
 */
public abstract class ModelObject {
    public abstract String getUsername();

    /**
     * Replaces current object ID with a new randomly generated one
     */
    public abstract void updateID();

    /**
     * @return randomly generated string to be used for IDs
     */
    protected String generateID() {
        char[] availableChars = {'1','2','3','4','5','6','7','8','9','0','a','b','c','d','e','f'};
        StringBuilder id = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 16; i++) {
            if ((i > 0) && ((i%4) == 0)) {
                id.append('-');
            }
            id.append(availableChars[random.nextInt(availableChars.length)]);
        }
        return id.toString();
    }
}
