package com.example.pastorabus.ui.mapa;



import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;

        import com.example.pastorabus.R;

public class MapaFragment extends AppCompatActivity {

    private MapaViewModel mapaViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_main2, container, false);
        return root;
    }


}
