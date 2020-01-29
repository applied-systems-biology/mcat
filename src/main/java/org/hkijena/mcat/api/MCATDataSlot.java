package org.hkijena.mcat.api;

public class MCATDataSlot<T extends MCATData> {
    private Class<T> acceptedDataType;
    private T data;

    public MCATDataSlot(Class<T> acceptedDataType) {
        this.acceptedDataType = acceptedDataType;
    }

    public Class<T> getAcceptedDataType() {
        return acceptedDataType;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
