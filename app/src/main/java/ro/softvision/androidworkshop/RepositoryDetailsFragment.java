package ro.softvision.androidworkshop;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ro.softvision.androidworkshop.model.Repository;


/**
 * A simple {@link Fragment} subclass.
 */
public class RepositoryDetailsFragment extends Fragment {
    private TextView mDescription, mPublic, mUrl, mHtmlUrl;
    private Repository mRepository;

    public static Fragment New(Repository repository) {
        Fragment f = new RepositoryDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("repository", repository);
        f.setArguments(args);
        return f;
    }

    public RepositoryDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        if (args != null && !args.isEmpty()) {
            mRepository = args.getParcelable("repository");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_repository_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDescription = (TextView) view.findViewById(R.id.description);
        mPublic = (TextView) view.findViewById(R.id.is_public);
        mUrl = (TextView) view.findViewById(R.id.url);
        mHtmlUrl = (TextView) view.findViewById(R.id.html_url);

        if (mRepository != null) {
            mDescription.setText(mRepository.getDescription());
            mPublic.setText(mRepository.getPrivate() ? "Private" : "Public");
            mUrl.setText(mRepository.getUrl());
            mHtmlUrl.setText(mRepository.getHtmlUrl());
        }
    }
}
