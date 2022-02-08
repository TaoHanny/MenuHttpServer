package com.shutuo.menuhttpserver;

import java.util.List;

public class MenuOrderData {
    
    public String method;
    public AppInfo appInfo;
    public String version;
    public List<Menu> menu;
    public Order order;
  
    public static class AppInfo {
      
        public String pkg;
        public String name;
        public String style;
    }
    
    public static class Order {
        public String foodAmount;
        public String promotionAmount;
        public String paidAmount;
        public String realAmount;
        public List<OrderData> orderData;

       
        public static class OrderData{
            
            public Integer orderStatus;
            public String foodCategoryName;
            public String foodSubType;
            public Integer isBatching;
            public String foodKey;
            public String foodName;
            public String setFoodName;
            public String setFoodRemark;
            public String foodNumber;
            public String unit;
            public Integer isDisCount;
            public Integer foodDiscountRate;
            public String foodProPrice;
            public String foodVipPrice;
            public String foodPayPrice;
            public String foodPayPriceReal;
            public String foodRemark;
            public String foodCode;
            public String foodAliasName;
            public String foodAmount;
            public String promotionAmount;
            public String paidAmount;
            public String realAmount;
        }
    }

    
    public static class Menu {
        
        public String foodCategoryCode;
        public String foodCategoryName;
        public String foodCode;
        public String foodName;
        public Boolean isRecommend;
        public String menuType;
        public List<Units> units;
        
        public static class Units {
            public String localValue;
            public String originalPrice;
            public String price;
            public String unit;
            public String unitAliasName;
            public String unitKey;
            public String vipPrice;
        }
    }
}
