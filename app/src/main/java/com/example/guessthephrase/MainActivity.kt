package com.example.guessthephrase

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

/*
- take a string and convert each letter into a star character ///////////////////////////////////////////
- ask the user to guess a predefined phrase
- ask the user to guess a letter from the phrase if they cannot guess the full phrase

- check the phrase for the guessed letter and convert stars into correctly guessed letters
- track guessed letters and display them to the user
- allow the user to guess the full phrase 10 times, the user should be able to enter a phrase during this stage
- allow the user to guess 10 letters, the user should only be able to enter single letters during this stage
- change the Entry Text hint to reflect whether the user is guessing the phrase or a letter
 */
class MainActivity : AppCompatActivity() {

    lateinit var tvPhrase:TextView  //
    lateinit var tvGuessdLitter:TextView //
    lateinit var phraseField:EditText //
    lateinit var butGuess:Button //

    val Phrase:String="Hello to my app"///
    val secretPhraseDictionary= mutableMapOf<Int,Char>()
    var secretPhrase=""//
    var userGuessLitter="" //
    var message: ArrayList<String> = ArrayList()
    var guessCountL =0 ////
    var guessCountP=0
    var guessPhrase=true///

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //- take a string and convert each letter into a star character
        for(i in Phrase.indices){
            if(Phrase[i].isWhitespace()){
                secretPhraseDictionary[i]=' '
                secretPhrase+=' '
            } else {
                secretPhraseDictionary[i]='*'
                secretPhrase+='*'
            }
        }

        tvPhrase=findViewById(R.id.tvPhrase)
        tvGuessdLitter=findViewById(R.id.tvCurrentGuessdLet)
        phraseField=findViewById(R.id.etPhraseGuess)
        butGuess=findViewById(R.id.butGuess)
        rv_phraseGame.adapter=RecyclerAdapter(message)
        rv_phraseGame.layoutManager = LinearLayoutManager(this)
        butGuess.setOnClickListener(){ addMsg()}

        //update secrete phrase (replace * with letter if correctly guessed) + Gussed letter (add all guessed letter) +
        //change field hint (one time user enter a phrse and the next time user enter letter and so on alternatively)
        updateString()

}//end on create

    fun updateString(){
        tvPhrase.text="Phrase: $secretPhrase"
        tvGuessdLitter.text="Guessed Letters: ${userGuessLitter.toUpperCase()}"
        if(guessPhrase){
            phraseField.hint="Guess the full phrase"
        }else
            phraseField.hint="Guess a letter"
    }//end updateString()

    fun addMsg(){
        val msg=phraseField.text.toString()
        // when gusse a phrase turn
        if (guessPhrase){
            //if phrase are equle
            if(msg.equals(Phrase,true)){
                disabledEntry()
                secretPhrase=Phrase
                updateString() // change the secret phrase to render the correct phrase on screen
                showAlertDialog("You win! \n \n Play again?")
            //if not
            }else{
                message.add("Wrong guess: $msg")
                GuessCountP()
                guessPhrase=false // to change the turn to guess a letter in updateString()
                updateString() //will check guessPhrase to change hint(no changes made on secret phrase and gusssed letter so wont chage)
            }
        //when gusse a letter trun
        }else{
            if (msg.isNotEmpty()&& msg.length==1){
                secretPhrase=""///////////
                guessPhrase=true//to change the turn to guess a phrase in updateString() (inside checkLetters())
                checkLetteres(msg[0]) //
            }else{
                Snackbar.make(findViewById(R.id.CL),"Plese enter one letter only",Snackbar.LENGTH_LONG).show()
            }
        }
        phraseField.text.clear()
        phraseField.clearFocus()
        rv_phraseGame.adapter?.notifyDataSetChanged()
    }//end adddMsg()

    fun checkLetteres(guessdLetter:Char){
        var found=0

        //to check if the letter exist in the Phrase
        //add the letter if it exist in the dictionary (to ???????
        for (i in Phrase.indices){
            if (Phrase[i].equals(guessdLetter,true)){
                secretPhraseDictionary[i]=guessdLetter
                found++
            }
        }//end for

        //update secretPhrase to be similar as secretPhraseDictionary
        //so that if a letter found in dictionary it will be updated
        for (i in secretPhraseDictionary){
            secretPhrase += secretPhraseDictionary[i.key]
        }//end for

        if (secretPhrase.equals(Phrase,true)){
            disabledEntry()
            showAlertDialog("You win!\\n\\nPlay again?")
        }//end if

        //update userGuessLitter
        if (userGuessLitter.isEmpty()){
            userGuessLitter += guessdLetter
        }else {
            userGuessLitter += " , " +guessdLetter
        }

        //to print how many letter found in the phrase
        if (found>0){
            message.add("Found $found ${guessdLetter.toUpperCase()}")
        }else {
            message.add("No ${guessdLetter.toUpperCase()} is found")
        }
        GuessCountL()
        updateString()
        rv_phraseGame.scrollToPosition(message.size-1)//////////////////////////
    }//end checkLetteres()

    fun GuessCountL(){
        guessCountL++
        val guessLeft= 10-guessCountL
        if (guessCountL<10){
            message.add("$guessLeft letter guesses remaining")
        }
    }//end GuessCountL()

    fun GuessCountP(){
        guessCountP++
        val guessLeft= 10-guessCountP
        if (guessCountP<10){
            message.add("$guessLeft Phrase guesses remaining")
        }
    }//GuessCountP()

    private fun disabledEntry(){
        butGuess.isEnabled = false
        butGuess.isClickable = false
        phraseField.isEnabled = false
        phraseField.isClickable = false
    }//disabledEntry()

    private fun showAlertDialog(title: String) {
        // build alert dialog
        val dialogBuilder = AlertDialog.Builder(this)

        // set message of alert dialog
        dialogBuilder.setMessage(title)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("Yes", DialogInterface.OnClickListener {
                    dialog, id -> this.recreate()
            })
            // negative button text and action
            .setNegativeButton("No", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Game Over")
        // show alert dialog
        alert.show()
    }//end showAlertDialog()

}



