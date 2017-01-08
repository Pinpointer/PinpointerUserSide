package com.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "pinpointer-mobilehub-1803401076-PinpointerCodes")

public class PinpointerCodesDO {
    private String _pinpointercode;
    private String _coordinatelist;

    @DynamoDBHashKey(attributeName = "pinpointercode")
    @DynamoDBAttribute(attributeName = "pinpointercode")
    public String getPinpointercode() {
        return _pinpointercode;
    }

    public void setPinpointercode(final String _pinpointercode) {
        this._pinpointercode = _pinpointercode;
    }
    @DynamoDBAttribute(attributeName = "coordinatelist")
    public String getCoordinatelist() {
        return _coordinatelist;
    }

    public void setCoordinatelist(final String _coordinatelist) {
        this._coordinatelist = _coordinatelist;
    }

}