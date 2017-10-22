package in.co.victor.chatbubblesdemo.model;

import android.app.Application;

/**
 * Created by Victor on 19/10/2017.
 */

public class Parameter extends Application {

    private Object obj;

    public void saveComplexObject(Object obj){
        this.obj = obj;
    }

    public Object getComplexObject(){
        return obj;
    }
}
