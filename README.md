# RiotAPI Contest 2018
#### Ken Wang @University of Utah & Mason Liu @University of California
##### *Uses the NA server API, API key will likely to be expired by the time you are using, please change the key at 
    src/application/ChampionFrequency.java

---

### To Recommend New Champion:

The program first gets champion information via [ddragon][link] . The version is 6.24.1. (New champion Neeko is not included in the document, so our program cannot recommend this champion.)
    
    A feature vector for each champion is created according to this document.
    Each feature vector contains 10 numeric values in this order:            
    (attack,magic,defense,difficulty,assassin,fighter,mage,support,tank,marksman)
    For the first 4 values, they are obtained from the official document directly.
    For the other 6 values, the program gets the tags of each champion from the document. Then, the value for each tag that champion owns  is calculated by 20/(number of tags that champion has). 
    For example: 
    Aatrox has tags "fighter" and "tank", so he has 20/2=10 on "fighter" and "tank" in his feature vector, and "assassin", "mage", "support" and "marksman" are all 0. 
    Akali only has one tag "assassin", so she has 20/1=20 on "assassin" in her feature vector, and 0 on other 5 tags.
    
After the user inputs his name, when program searches his past matches, it calculates his Player Feature Vector. For each match, the program finds the champion he played in that match, and add that champion's feature vector to his Player Feature Vector. After all matches are parsed, we divide his Player Feature Vector by the number of matches to get the average.

When the user wants to pick a new champion, the program finds the distance between his Player Feature Vector with each of the champion's feature vector. The distance is calculated by Pythagorean Theorem (for each feature, calculate the square of difference, and sum the squares up). 

However, for the "difficulty" feature, a player who often plays "hard" champions can handle new "easy" champions, while a player who often plays "easy" champions might not be able to handle new "hard" champions. So for calculating the distance of "difficulty" feature, the program uses the following formula: 
    
    ("Difficulty" of Player Feature Vector-"Difficulty" of champion's feature vector )
    Multiply it by 2 (since difficulty can largely affect playing experience for unfamiliar champions)
    Add the result to the Pythagorean Distance of the other 9 features.

Then, the program recommends 5 champions with the shortest distance.

---

### To Recommend Old Champion:

The program aims to recommend champions that player used awhile ago and have a above-average KDA. This is to let old league players like me (Since 2011) to have a better choice in terms of champion selection, The main reason is me and my friends as old league players often don't know which champ to play (not in a rank game), we know almost every details of each champ and stuck into a position wondering which champ to play. Thus the idea of recommending old champ is to let these kind of players to get a good gaming experience with some specific champ that the program recommend. 

The formula to pick a old champ is based on numerizing the 

       Average K/D/A, Win/Lose,playing frequency,and how long since last played.
       
And used the latest 98 games as the data input (Limited to the API constraint). Regretly there's no document nor enough data points to supports that this formula will give a good recommendation, it's solely based on the authors' subjectivity.   

---

### Some screenshot of the program
![PIC](https://github.com/gzmason/RiotAPI/blob/master/Pics/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20190123160716.png)
![PIC](https://github.com/gzmason/RiotAPI/blob/master/Pics/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_201901231607161.png)
![PIC](https://github.com/gzmason/RiotAPI/blob/master/Pics/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_201901231607162.png)

 [link]: <http://ddragon.leagueoflegends.com/cdn/6.24.1/data/en_US/champion.json>
