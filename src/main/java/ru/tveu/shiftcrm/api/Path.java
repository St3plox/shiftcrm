package ru.tveu.shiftcrm.api;

public class Path {

    public static final String VERSION = "/v1";
    public static final String ROOT = "/api" + VERSION;
    public static final String ID = "/{id}";

    public static final String SELLER = ROOT + "/seller";
    public static final String SELLER_GET = SELLER + ID;
    public static final String SELLER_GET_ALL = SELLER;
    public static final String SELLER_POST = SELLER;
    public static final String SELLER_PUT = SELLER;
    public static final String SELLER_DELETE = SELLER + ID;

    public static final String SELLER_GET_MOST_PRODUCTIVE = SELLER + "/most-productive";
    public static final String SELLER_GET_WITH_TX_BELOW_THRESHOLD = SELLER + "/below-threshold";
    public static final String SELLER_GET_BEST_PERIOD = SELLER + "/best-period";

    public static final String TRANSACTION = ROOT + "/transaction";
    public static final String TRANSACTION_GET = TRANSACTION + ID;
    public static final String TRANSACTION_GET_BY_SELLER = TRANSACTION;
    public static final String TRANSACTION_POST = TRANSACTION;

}
