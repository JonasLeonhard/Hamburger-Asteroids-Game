package com.a420.methlab.badtrip;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/** @brief Gameover Bildschirm
 *
 * Wird aufgerufen sobald das Spaceship des Players auf !isAlive gesetzt wird.
 * Es erhält die scorePoints des GameFragments und zeigt diese An.
 * Im ButtonListener wird bei OnClick() auf das MenuFragment gewechselt
 */
public class GameOverFragment extends Fragment {

    int scorePoints;
    List<Integer> scoreBoard;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.gameover, container, false);

        ((TextView)contentView.findViewById(R.id.score_text)).setText(String.valueOf(scorePoints));

        final MainActivity act = (MainActivity)getActivity();
        ((Button)contentView.findViewById(R.id.score_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scoreBoard.add(scorePoints);
                Log.d("HAllo", "Eigentlich kommt jetzt der Wechsel");
                act.switchToMenu();
            }
        });

        return contentView;
    }

    /** @brief setzt den Punktestand des Spielers
     *
     * einfacher setter
     *
     * @param score Der Wert, der eingestellt werden soll
     */
    public void setScoreValue(int score) {
        this.scorePoints = score;
    }

    public void setScoreBoardList(List<Integer> scoreBoard) {
        this.scoreBoard = scoreBoard;
    }

}
