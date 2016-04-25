package Modules;

import java.util.List;

/**
 * Created by vinh on 23/04/2016.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
