package ru.mirea.aleksandrovnd.mirea_project;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class BackgroundTaskFragment extends Fragment {

    private TextView tvStatus;
    private Button btnStart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_background_task, container, false);
        tvStatus = view.findViewById(R.id.tvTaskStatus);
        btnStart = view.findViewById(R.id.btnStartTask);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnStart.setOnClickListener(v -> {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                    // .setConstraints(constraints)
                    .build();

            WorkManager.getInstance(requireContext()).enqueue(workRequest);
            tvStatus.setText("Задача запущена, ожидание...");

            WorkManager.getInstance(requireContext())
                    .getWorkInfoByIdLiveData(workRequest.getId())
                    .observe(getViewLifecycleOwner(), new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null) {
                                if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                                    tvStatus.setText("Задача выполнена успешно!");
                                    Toast.makeText(getContext(), "Фоновая задача завершена", Toast.LENGTH_SHORT).show();
                                } else if (workInfo.getState() == WorkInfo.State.FAILED) {
                                    tvStatus.setText("Задача завершилась ошибкой");
                                    Toast.makeText(getContext(), "Ошибка выполнения задачи", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        });
    }
}
