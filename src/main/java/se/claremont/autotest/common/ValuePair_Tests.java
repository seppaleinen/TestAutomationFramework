package se.claremont.autotest.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the ValuePair class
 *
 * Created by jordam on 2016-09-18.
 */
public class ValuePair_Tests {

    @Test
    public void createAndRead(){
        ValuePair valuePair = new ValuePair("Name", "Value");
        Assert.assertTrue(valuePair.parameter.equals("Name"));
        Assert.assertTrue(valuePair.value.equals("Value"));
    }

    @Test
    public void update(){
        ValuePair valuePair = new ValuePair("Name", "Value");
        valuePair.reassign("Value2");
        Assert.assertTrue(valuePair.value.equals("Value2"));
    }

    @SuppressWarnings("UnusedAssignment")
    @Test
    public void nulls(){
        try {
            ValuePair valuePair = new ValuePair(null, null);
        }catch (Exception e){
            Assert.assertTrue("Cannot instantiate ValuePair with null values", false);
        }
    }

    @Test
    public void integers(){
        ValuePair valuePair = new ValuePair("Parameter name", 123);
        Assert.assertTrue(valuePair.value.equals("123"));
    }
}
