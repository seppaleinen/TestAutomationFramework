package se.claremont.tools;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by magnusolsson on 2016-09-23.
 */
public class UtilsTest{

    @Test
    public void getInstance(){
        Assert.assertTrue( Utils.getInstance() != null );
    }

    @Test
    public void getOS() {
        Assert.assertTrue( !Utils.getInstance().getOS().equalsIgnoreCase("") );
        Assert.assertTrue( Utils.getInstance().amIMacOS() ||
                Utils.getInstance().getOS().toLowerCase().contains("linux") ||
                Utils.getInstance().amIWindowsOS() );
    }

}