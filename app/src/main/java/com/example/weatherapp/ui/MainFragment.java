package com.example.weatherapp.ui;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.weatherapp.data.WeatherDataRepo;
import com.example.weatherapp.data.datamodels.WeatherData;
import com.example.weatherapp.data.entities.LocationWeatherData;
import com.example.weatherapp.viewmodels.MainViewModel;
import com.example.weatherapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;

    @BindView(R.id.editText)
    EditText locationET;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.main_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        locationET.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    LocationWeatherData data = mViewModel.getLocationWratherData(locationET.getText().toString().trim());
                    if(data!= null){
                        mViewModel.getWeatherData().postValue(data.weatherData);
                    }
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // TODO: Use the ViewModel


        mViewModel.getWeatherData().observe(this, new Observer<WeatherData>() {
            @Override
            public void onChanged(WeatherData weatherData) {
                if(weatherData!= null){
                    Toast.makeText(getContext(), weatherData.getCurrent().getCondition().getText(), Toast.LENGTH_LONG).show();

                }
            }
        });

        mViewModel.getDataFetchRequestStatus().observe(MainFragment.this, new Observer<WeatherDataRepo.STATUS>() {
            @Override
            public void onChanged(WeatherDataRepo.STATUS status) {

                switch (status){

                    case REQUESTED:

                        break;
                    case FAILURE:
                        Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_LONG).show();
                        break;
                    case INVALID:
                        Toast.makeText(getContext(), getString(R.string.invalid), Toast.LENGTH_LONG).show();
                        break;
                    case SUCCESS:
                        break;

                }



            }
        });
    }

}
