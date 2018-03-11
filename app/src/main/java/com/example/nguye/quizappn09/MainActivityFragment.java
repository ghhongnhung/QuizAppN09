package com.example.nguye.quizappn09;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final int SO_LOAI_VAT_TRONG_BAI_QUIZ = 10;

    private List<String> danhSachTenTatCaLoaiVat;
    private List<String> danhSachTenLoaiVatTrongQuiz;
    private Set<String> kieuLoaiVatTrongQuiz;
    private String cauTraLoiDung;
    private int soLanDoan;
    private int soCauTraLoiDung;
    private int soHangDoanTenDongVat;
    private SecureRandom secureRandomNumber;
    private Handler handler;
    private Animation wrongAnswerAnimation;

    private LinearLayout animalQuizLinearLayout;
    private TextView txtQuestionNumber;
    private ImageView imgAnimal;
    private LinearLayout[] soHangNutDoanTrongQuiz;
    private TextView txtAnswer;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        danhSachTenTatCaLoaiVat = new ArrayList<>();
        danhSachTenLoaiVatTrongQuiz = new ArrayList<>();
        secureRandomNumber = new SecureRandom();
        handler = new Handler();

        wrongAnswerAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.wrong_answer_animation);
        wrongAnswerAnimation.setRepeatCount(1);

        animalQuizLinearLayout = (LinearLayout) view.findViewById(R.id.animalQuizLinearLayout);
        txtQuestionNumber = (TextView) view.findViewById(R.id.txtQuestionNumber);
        imgAnimal = (ImageView) view.findViewById(R.id.imgAnimal);
        soHangNutDoanTrongQuiz = new LinearLayout[3];
        soHangNutDoanTrongQuiz[0] = (LinearLayout) view.findViewById(R.id.firstRowLinearLayout);
        soHangNutDoanTrongQuiz[1] = (LinearLayout) view.findViewById(R.id.secondRowLinearLayout);
        soHangNutDoanTrongQuiz[2] = (LinearLayout) view.findViewById(R.id.thirdRowLinearLayout);
        txtAnswer = (TextView) view.findViewById(R.id.txtAnswer);

        for (LinearLayout hang : soHangNutDoanTrongQuiz) {

            for (int cot = 0; cot < hang.getChildCount(); cot++) {

                Button btnGuess = (Button) hang.getChildAt(cot);
                btnGuess.setOnClickListener(btnGuessListener);
                btnGuess.setTextSize(24);
            }
        }

        txtQuestionNumber.setText(getString(R.string.question_text, 1, SO_LOAI_VAT_TRONG_BAI_QUIZ));

        return view;
    }

    private View.OnClickListener btnGuessListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            Button btnGuess = ((Button) view);
            String guessValue = btnGuess.getText().toString();
            String answerValue = layTenChinhXacLoaiVat(cauTraLoiDung);
            ++soLanDoan;

            if (guessValue.equals(answerValue)) {

                ++soCauTraLoiDung;

                txtAnswer.setText("Là " + answerValue + "! Bạn đã trả lời đúng!");

                disableGuessButton();

                if (soCauTraLoiDung == SO_LOAI_VAT_TRONG_BAI_QUIZ) {

                    MyAlertDialogFragment ketQua = MyAlertDialogFragment.newInstance(soLanDoan);
                    ketQua.setCancelable(false);
                    ketQua.show(getFragmentManager(), "KetQua");

                } else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animateQuiz(true);
                        }
                    }, 1000);
                }
            } else {

                imgAnimal.startAnimation(wrongAnswerAnimation);

                txtAnswer.setText(R.string.wrong_answer_message);
                btnGuess.setEnabled(false);
            }
        }
    };

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int soLanDoan) {

            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("SoLanDoan", soLanDoan);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int soLanDoan = getArguments().getInt("SoLanDoan");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.results_string_value, soLanDoan, (1000/ (double) soLanDoan)));
            builder.setPositiveButton(R.string.reset_animal_quiz, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MainActivityFragment) getFragmentManager().findFragmentById(R.id.animalQuizFragment)).resetQuiz();
                }
            });

            return builder.create();
        }

    }

    private String layTenChinhXacLoaiVat(String tenLoaiVat) {

        return tenLoaiVat.substring(tenLoaiVat.indexOf('-') + 1).replace('_', ' ');
    }

    private void disableGuessButton() {

        for (int hang = 0; hang < soHangDoanTenDongVat; hang++) {

            LinearLayout guessRowLinearLayout = soHangNutDoanTrongQuiz[hang];

            for (int btnIndex = 0; btnIndex < guessRowLinearLayout.getChildCount(); btnIndex++) {

                guessRowLinearLayout.getChildAt(btnIndex).setEnabled(false);
            }
        }
    }


    private void animateQuiz(boolean animateOutAnimalImage) {

        if (soCauTraLoiDung == 0) {

            return;

        }

        int xTopLeft = 0;
        int yTopLeft = 0;


        int xBottomRight = animalQuizLinearLayout.getLeft() + animalQuizLinearLayout.getRight();
        int yBottomRight = animalQuizLinearLayout.getTop() + animalQuizLinearLayout.getBottom();

        int radius = Math.max(animalQuizLinearLayout.getWidth(), animalQuizLinearLayout.getHeight());

        Animator animator;

        if (animateOutAnimalImage) {

            animator = ViewAnimationUtils.createCircularReveal(animalQuizLinearLayout,
                    xBottomRight, yBottomRight, radius, 0);


            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    xemDongVatTiepTheo();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });


        } else {

            animator = ViewAnimationUtils.createCircularReveal(animalQuizLinearLayout,
                    xTopLeft, yTopLeft, 0, radius);

        }

        animator.setDuration(700);
        animator.start();

    }

    public void resetQuiz() {

        AssetManager assets = getActivity().getAssets();
        danhSachTenTatCaLoaiVat.clear();

        try {
            for (String kieuLoaiVat : kieuLoaiVatTrongQuiz) {

                String[] animalImagePaths = assets.list(kieuLoaiVat);

                for (String animalImagePath : animalImagePaths) {

                    danhSachTenTatCaLoaiVat.add(animalImagePath.replace(".png", ""));
                }
            }
        } catch (IOException e) {
            Log.e("TracNghiemDongVat", "Lỗi", e);
        }

        soCauTraLoiDung = 0;
        soLanDoan = 0;
        danhSachTenLoaiVatTrongQuiz.clear();

        int count = 1;
        int soLuongDongVatSanCo = danhSachTenTatCaLoaiVat.size();

        while (count <= SO_LOAI_VAT_TRONG_BAI_QUIZ) {

            int randomIndex = secureRandomNumber.nextInt(soLuongDongVatSanCo);

            String animalImageName = danhSachTenTatCaLoaiVat.get(randomIndex);

            if (!danhSachTenLoaiVatTrongQuiz.contains(animalImageName)) {

                danhSachTenLoaiVatTrongQuiz.add(animalImageName);
                ++count;
            }
        }

        xemDongVatTiepTheo();
    }

    private void xemDongVatTiepTheo() {

        String tenAnhDongVatTiepTheo = danhSachTenLoaiVatTrongQuiz.remove(0);
        cauTraLoiDung = tenAnhDongVatTiepTheo;
        txtAnswer.setText("");
        txtQuestionNumber.setText(getString(R.string.question_text, (soCauTraLoiDung + 1), SO_LOAI_VAT_TRONG_BAI_QUIZ));
        String kieuDongVat = tenAnhDongVatTiepTheo.substring(0, tenAnhDongVatTiepTheo.indexOf("-"));

        AssetManager assets = getActivity().getAssets();

        try (InputStream stream = assets.open(kieuDongVat + "/" + tenAnhDongVatTiepTheo + ".png")) {

            Drawable anhDongVat = Drawable.createFromStream(stream, tenAnhDongVatTiepTheo);

            imgAnimal.setImageDrawable(anhDongVat);

            animateQuiz(false);
        } catch (IOException e) {

            Log.e("TracNghiemDongVat", "Có lỗi khi lấy ảnh " + tenAnhDongVatTiepTheo, e);
        }

        Collections.shuffle(danhSachTenTatCaLoaiVat);

        int tenLoaiVatDungIndex = danhSachTenTatCaLoaiVat.indexOf(cauTraLoiDung);
        String tenLoaiVatDung = danhSachTenTatCaLoaiVat.remove(tenLoaiVatDungIndex);
        danhSachTenTatCaLoaiVat.add(tenLoaiVatDung);

        for (int hang = 0;hang < soHangDoanTenDongVat; hang++) {

            for (int cot = 0; cot < soHangNutDoanTrongQuiz[hang].getChildCount(); cot++) {

                Button btnGuess = (Button) soHangNutDoanTrongQuiz[hang].getChildAt(cot);
                btnGuess.setEnabled(true);

                String tenAnhDongVat = danhSachTenTatCaLoaiVat.get((hang*2) + cot);
                btnGuess.setText(layTenChinhXacLoaiVat(tenAnhDongVat));
            }
        }

        int hang = secureRandomNumber.nextInt(soHangDoanTenDongVat);
        int cot = secureRandomNumber.nextInt(2);
        LinearLayout randomRow = soHangNutDoanTrongQuiz[hang];
        String tenAnhDongVatDung = layTenChinhXacLoaiVat(cauTraLoiDung);
        ((Button) randomRow.getChildAt(cot)).setText(tenAnhDongVatDung);
    }

    public void editSoHangDoanTenDongVat (SharedPreferences sharedPreferences) {

        final String SO_TUY_CHON_DOAN = sharedPreferences.getString(MainActivity.GUESSES, null);
        soHangDoanTenDongVat = Integer.parseInt(SO_TUY_CHON_DOAN) / 2;

        for (LinearLayout horizontalLinearLayout : soHangNutDoanTrongQuiz) {

            horizontalLinearLayout.setVisibility(View.GONE);
        }

        for (int hang = 0; hang < soHangDoanTenDongVat; hang++) {

            soHangNutDoanTrongQuiz[hang].setVisibility(View.VISIBLE);
        }
    }

    public void suaLoaiDongVatTrongQuiz (SharedPreferences sharedPreferences) {

        kieuLoaiVatTrongQuiz = sharedPreferences.getStringSet(MainActivity.ANIMAL_TYPES, null);
    }

    public void thayDoiFontChu (SharedPreferences sharedPreferences) {

        String fontStringValue = sharedPreferences.getString(MainActivity.QUIZ_FONT, null);

        switch (fontStringValue) {

            case "UVNBanhMi.TTF":
                for (LinearLayout hang : soHangNutDoanTrongQuiz) {

                    for (int cot = 0; cot < hang.getChildCount(); cot++) {

                        Button button = (Button) hang.getChildAt(cot);
                        button.setTypeface(MainActivity.UVNBanhMi);
                    }
                }
                break;

            case "UVNSachVo_R.TTF":
                for (LinearLayout hang : soHangNutDoanTrongQuiz) {

                    for (int cot = 0; cot < hang.getChildCount(); cot++) {

                        Button button = (Button) hang.getChildAt(cot);
                        button.setTypeface(MainActivity.UVNSachVo_R);
                    }
                }
                break;

            case "windsorb.ttf":
                for (LinearLayout hang : soHangNutDoanTrongQuiz) {

                    for (int cot = 0; cot < hang.getChildCount(); cot++) {

                        Button button = (Button) hang.getChildAt(cot);
                        button.setTypeface(MainActivity.windsorb);
                    }
                }
                break;
        }
    }

    public void editBackgroundColor (SharedPreferences sharedPreferences) {

        String backgroundColor = sharedPreferences.getString(MainActivity.QUIZ_BACKGROUND_COLOR, null);

        switch (backgroundColor) {

            case "Trắng":
                animalQuizLinearLayout.setBackgroundColor(Color.WHITE);

                for (LinearLayout hang : soHangNutDoanTrongQuiz) {

                    for (int cot = 0; cot < hang.getChildCount(); cot++) {

                        Button button = (Button) hang.getChildAt(cot);
                        button.setBackgroundColor(Color.GREEN);
                        button.setTextColor(Color.WHITE);
                    }
                }

                txtAnswer.setTextColor(Color.BLUE);
                txtQuestionNumber.setTextColor(Color.BLACK);

                break;

            case "Đen":
                animalQuizLinearLayout.setBackgroundColor(Color.BLACK);

                for (LinearLayout hang : soHangNutDoanTrongQuiz) {

                    for (int cot = 0; cot < hang.getChildCount(); cot++) {

                        Button button = (Button) hang.getChildAt(cot);
                        button.setBackgroundColor(Color.YELLOW);
                        button.setTextColor(Color.BLACK);
                    }
                }

                txtAnswer.setTextColor(Color.YELLOW);
                txtQuestionNumber.setTextColor(Color.YELLOW);

                break;

            case "Xanh lá":
                animalQuizLinearLayout.setBackgroundColor(Color.GREEN);

                for (LinearLayout hang : soHangNutDoanTrongQuiz) {

                    for (int cot = 0; cot < hang.getChildCount(); cot++) {

                        Button button = (Button) hang.getChildAt(cot);
                        button.setBackgroundColor(Color.RED);
                        button.setTextColor(Color.WHITE);
                    }
                }

                txtAnswer.setTextColor(Color.RED);
                txtQuestionNumber.setTextColor(Color.RED);

                break;

            case "Đỏ":
                animalQuizLinearLayout.setBackgroundColor(Color.RED);

                for (LinearLayout hang : soHangNutDoanTrongQuiz) {

                    for (int cot = 0; cot < hang.getChildCount(); cot++) {

                        Button button = (Button) hang.getChildAt(cot);
                        button.setBackgroundColor(Color.BLUE);
                        button.setTextColor(Color.WHITE);
                    }
                }

                txtAnswer.setTextColor(Color.WHITE);
                txtQuestionNumber.setTextColor(Color.WHITE);

                break;

            case "Xanh dương":
                animalQuizLinearLayout.setBackgroundColor(Color.BLUE);

                for (LinearLayout hang : soHangNutDoanTrongQuiz) {

                    for (int cot = 0; cot < hang.getChildCount(); cot++) {

                        Button button = (Button) hang.getChildAt(cot);
                        button.setBackgroundColor(Color.RED);
                        button.setTextColor(Color.WHITE);
                    }
                }

                txtAnswer.setTextColor(Color.WHITE);
                txtQuestionNumber.setTextColor(Color.WHITE);

                break;

            case "Vàng":
                animalQuizLinearLayout.setBackgroundColor(Color.YELLOW);

                for (LinearLayout hang : soHangNutDoanTrongQuiz) {

                    for (int cot = 0; cot < hang.getChildCount(); cot++) {

                        Button button = (Button) hang.getChildAt(cot);
                        button.setBackgroundColor(Color.BLACK);
                        button.setTextColor(Color.YELLOW);
                    }
                }

                txtAnswer.setTextColor(Color.BLACK);
                txtQuestionNumber.setTextColor(Color.BLACK);

                break;
        }
    }
}
