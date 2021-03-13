package com.example.fitastic;

import java.util.ArrayList;

public class rewardsGenerator {

    public static ArrayList<rewardsGenerator> rewards;
    //Arraylist to hold brands
    private ArrayList<String> brandsList;
    //Arraylist to hold amount of discount
    private ArrayList<String> discountList;
    //Arraylist to hold amount of points required
    private ArrayList<String> pointsList;
    //Arraylist to hold type of items
    private ArrayList<String> typeList;

    private String discount;
    private String brands;
    private String points;
    private String type;

    rewardsGenerator(int tier){

        discountList = new ArrayList<>();
        pointsList = new ArrayList<>();
        addBrandsandTypes();

        switch(tier){
            case 0:
                for(int i = 0; i < 5; i++){
                    //Generate 5-10% discounts
                    int disc = (int)((Math.random() * (10-5)) + 5);
                    discountList.add(String.valueOf(disc));
                    int p = (int)((Math.random()*(500-100)) + 100);
                    pointsList.add(String.valueOf(p));
                }

                setDiscount(getDiscount(discountList));
                setPoints(getPoints(pointsList));
                setBrands(getBrand(brandsList));
                setType(getType(typeList));
                break;

            case 1:
                for(int i = 0; i < 5; i++){
                    //Generate 5-10% discounts
                    int disc = (int)((Math.random() * (12-5)) + 5);
                    discountList.add(String.valueOf(disc));
                    int p = (int)((Math.random()*(700-300)) + 300);
                    pointsList.add(String.valueOf(p));
                }

                setDiscount(getDiscount(discountList));
                setPoints(getPoints(pointsList));
                setBrands(getBrand(brandsList));
                setType(getType(typeList));
                break;

            case 2:

                for(int i = 0; i < 5; i++){
                    //Generate 5-10% discounts
                    int disc = (int)((Math.random() * (15-5)) + 5);
                    discountList.add(String.valueOf(disc));
                    int p = (int)((Math.random()*(1000-500)) + 500);
                    pointsList.add(String.valueOf(p));
                }

                setDiscount(getDiscount(discountList));
                setPoints(getPoints(pointsList));
                setBrands(getBrand(brandsList));
                setType(getType(typeList));
                break;
        }
    }

    public void addBrandsandTypes(){
        brandsList = new ArrayList<>();

        typeList = new ArrayList<>();

        brandsList.add("Nike");
        brandsList.add("Under Armour");
        brandsList.add("Adidas");
        brandsList.add("Asics");
        brandsList.add("Hoka One One");
        brandsList.add("Puma");
        brandsList.add("New Balance");
        brandsList.add("Salomon");
        brandsList.add("Lululemon");
        brandsList.add("Patagonia");

        typeList.add("Tops");
        typeList.add("Bottoms");
        typeList.add("Accessories");
        typeList.add("Footwear");
    }



    //Generate Rewards
    public static ArrayList generate(int tier){
        rewards = new ArrayList<>();
        rewards.add(new rewardsGenerator(1));

        return rewards;
    }

    public static String getDiscount(ArrayList<String> a){
        return a.get((int)(Math.random() * (5-1) + 1));
    }
    public static String getPoints(ArrayList<String> a){
        return a.get((int)(Math.random() * (5-1) + 1));
    }

    public static String getBrand(ArrayList<String> a){
        return a.get((int)(Math.random() * (10-1) + 1));
//        return a.get(3);
    }

    public static String getType(ArrayList<String> a){
        return a.get((int)(Math.random() * (4-1) + 1));
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getBrands() {
        return brands;
    }

    public void setBrands(String brands) {
        this.brands = brands;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
