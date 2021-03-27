package com.example.fitastic;

import java.util.ArrayList;

public class ChallengeGenerator {

    public static ArrayList<ChallengeGenerator> challenges;
    public String challenge;
    public int points;


   ChallengeGenerator(int index){
       switch(index){
           case 1:
               setChallenge("Beat your personal best pace");
               setPoints(1000);
               break;
           case 2:
               setChallenge("Complete a 5km run");
               setPoints(1000);
               break;

               case 3:
                   setChallenge("Complete 3 runs");
                   setPoints(1000);
               break;

           case 4:
               setChallenge("Complete a 10km run");
               setPoints(1000);
               break;
       }


   }

   public static ArrayList generateChallenges(){
       challenges = new ArrayList<ChallengeGenerator>();
       for(int i = 0; i < 4; i++){
           int x = (int) (Math.random() * 4 - 1) + 1;
           challenges.add(new ChallengeGenerator(x));
       }
       System.out.println(challenges.get(0).getChallenge());
       return challenges;
       
       

   }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

}
