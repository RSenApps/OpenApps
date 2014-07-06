package com.dydxtech.openapps;

import java.util.List;

/**
 * Created by Ryan on 6/22/2014.
 */
public interface SpeechRecognizerListener {
    /**
     * @param heard
     * @return True to stop listening
     */
    public boolean onHeard(List<String> heard);
}
