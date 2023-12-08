package com.example.quizletfinal.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.quizletfinal.R;
import com.example.quizletfinal.models.UserAnswer;

import java.util.List;

public class UserAnswerAdapter extends BaseAdapter {

    private Context context;
    private List<UserAnswer> userAnswers;

    public UserAnswerAdapter(Context context, List<UserAnswer> userAnswers) {
        this.context = context;
        this.userAnswers = userAnswers;
    }

    @Override
    public int getCount() {
        return userAnswers.size();
    }

    @Override
    public Object getItem(int position) {
        return userAnswers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.card_review_layout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }



        UserAnswer userAnswer = userAnswers.get(position);

        holder.txtWordFront.setText(userAnswer.getEnglish());
        holder.txtCorrectAnswer.setText(userAnswer.getVietnamese());
        holder.txtInCorrectAnswer.setText(userAnswer.getSelectedAnswer());
        holder.txtIsCorrect.setText(userAnswer.isCorrect() ? "Correct" : "Incorrect");
        holder.ifCorrect.setVisibility(userAnswer.isCorrect() ? View.GONE : View.VISIBLE);
        holder.txtIsCorrect.setTextColor(userAnswer.isCorrect() ? context.getResources().getColor(android.R.color.black) : context.getResources().getColor(android.R.color.white));
        holder.isCorrectBackground.setBackgroundColor(userAnswer.isCorrect() ? Color.GREEN : Color.RED);
        return convertView;
    }

    private static class ViewHolder {
        TextView txtWordFront;
        TextView txtCorrectAnswer;
        TextView txtInCorrectAnswer;
        TextView txtIsCorrect;
        LinearLayout isCorrectBackground, ifCorrect;

        ViewHolder(View view) {
            txtWordFront = view.findViewById(R.id.txtWordFront);
            txtCorrectAnswer = view.findViewById(R.id.txtCorrectAnswer);
            txtInCorrectAnswer = view.findViewById(R.id.txtInCorrectAnswer);
            txtIsCorrect = view.findViewById(R.id.txtIsCorrect);
            isCorrectBackground = view.findViewById(R.id.isCorrectBackground);
            ifCorrect = view.findViewById(R.id.ifCorrect);
        }
    }
}
