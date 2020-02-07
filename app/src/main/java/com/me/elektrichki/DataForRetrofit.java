package com.me.elektrichki;

public class DataForRetrofit {

  private   String nameFrom;
  private   String nameTo;
  private   String codeFrom;
  private   String codeTo;
  private int itemId;
  private   int routeId;


    public DataForRetrofit(String nameFrom, String nameTo, String codeFrom, String codeTo, int itemId, int routeId) {
        this.nameFrom = nameFrom;
        this.nameTo = nameTo;
        this.codeFrom = codeFrom;
        this.codeTo = codeTo;
        this.itemId = itemId;
        this.routeId = routeId;
    }


    public String getNameFrom() {
        return nameFrom;
    }

    public void setNameFrom(String nameFrom) {
        this.nameFrom = nameFrom;
    }

    public String getNameTo() {
        return nameTo;
    }

    public void setNameTo(String nameTo) {
        this.nameTo = nameTo;
    }

    public String getCodeFrom() {
        return codeFrom;
    }

    public void setCodeFrom(String codeFrom) {
        this.codeFrom = codeFrom;
    }

    public String getCodeTo() {
        return codeTo;
    }

    public void setCodeTo(String codeTo) {
        this.codeTo = codeTo;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}
