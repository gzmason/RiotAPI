# RiotAPI
*Uses the NA server API

To Recommend New Champion:

The program first gets champion information via http://ddragon.leagueoflegends.com/cdn/6.24.1/data/en_US/champion.json. The version is 6.24.1. (New champion Neeko is not included in the document, so our program cannot recommend this champion.)
    
    A feature vector for each champion is created according to this document.
    Each feature vector contains 10 numeric values in this order:            
    (attack,magic,defense,difficulty,assassin,fighter,mage,support,tank,marksman)
    For the first 4 values, they are obtained from the official document directly.
    For the other 6 values, the program gets the tags of each champion from the document. Then, the value for each tag that champion owns   is calculated by 20/(number of tags that champion has). For example, Aatrox has tags "fighter" and "tank", so he has 20/2=10 on           "fighter" and "tank" in his feature vector, and "assassin", "mage", "support" and "marksman" are all 0. On the other hand, Akali only     has one tag "assassin", so she has 20/1=20 on "assassin" in her feature vector, and 0 on other 5 tags.
    
After the user inputs his name, when program searches his past matches, it calculates his Player Feature Vector. For each match, the program finds the champion he played in that match, and add that champion's feature vector to his Player Feature Vector. After all matches are parsed, we divide his Player Feature Vector by the number of matches to get the average.

When the user wants to pick a new champion, the program finds the distance between his Player Feature Vector with each of the champion's feature vector. The distance is calculated by Pythagorean Theorem (for each feature, calculate the square of difference, and sum the squares up). 

However, for the "difficulty" feature, a player who often plays "hard" champions can handle new "easy" champions, while a player who often plays "easy" champions might not be able to handle new "hard" champions. So for calculating the distance of "difficulty" feature, the program uses the following formula: 
    
    ("Difficulty" of Player Feature Vector-"Difficulty" of champion's feature vector ), multiply it by 2 (since difficulty can largely affect playing experience for unfamiliar champions), and add the result to the Pythagorean Distance of the other 9 features.

Then, the program recommends 5 champions with the shortest distance.
