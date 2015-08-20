package com.swarmnyc.pup.Services;

import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.RestApis.RestApiCallback;
import com.swarmnyc.pup.RestApis.UserRestApi;
import com.swarmnyc.pup.StringUtils;
import com.swarmnyc.pup.models.CurrentUserInfo;
import com.swarmnyc.pup.models.SocialMedium;
import com.swarmnyc.pup.viewmodels.UserRequestResult;

import retrofit.client.Response;
import retrofit.mime.TypedFile;

import java.io.File;
import java.util.Date;

public class UserServiceImpl implements UserService {
    private static final String PASSWORD = "swarmnyc";
    private UserRestApi m_userApi;

    public UserServiceImpl(UserRestApi userApi) {
        m_userApi = userApi;
    }

    @Override
    public void login(
            final String email, final ServiceCallback<CurrentUserInfo> callback
    ) {
        m_userApi.login(
                email, PASSWORD, new RestApiCallback<UserRequestResult>(null) {
                    @Override
                    public void success(final UserRequestResult userRequestResult, final Response response) {
                        if (userRequestResult.isSuccess()) {
                            if (callback != null) {
                                callback.success(userRequestResult.getUser());
                            }
                        } else {
                            EventBus.getBus().post(new RuntimeException(userRequestResult.getErrorMessage()));
                        }
                    }
                }
        );
    }

    @Override
    public void register(
            final String email, final String username, String file, final ServiceCallback<CurrentUserInfo> callback
    ) {
        TypedFile tf = null;
        if (StringUtils.isNotEmpty(file)) {
            tf = new TypedFile("multipart/form-data", new File(file));
        }

        m_userApi.register(
                email, PASSWORD, username, tf, new RestApiCallback<UserRequestResult>(null) {
                    @Override
                    public void success(final UserRequestResult userRequestResult, final Response response) {
                        if (userRequestResult.isSuccess()) {
                            if (callback != null) {
                                callback.success(userRequestResult.getUser());
                            }
                        } else {
                            if (callback != null) {
                                callback.failure(userRequestResult.getErrorMessage());
                            }
                        }
                    }
                }
        );
    }

    @Override
    public void updatePortrait(final String file, final ServiceCallback<String> callback) {
        TypedFile tf = null;
        if (StringUtils.isNotEmpty(file)) {
            tf = new TypedFile("multipart/form-data", new File(file));
        }

        m_userApi.updatePortrait(tf, new RestApiCallback<>(callback));
    }

    @Override
    public void deleteMedium(final String type, final ServiceCallback<String> callback) {
        m_userApi.deleteMedium(type, new RestApiCallback<>(callback));
    }

    @Override
    public void addMedium(
            final String type,
            final String userId,
            final String token,
            final String secret,
            final Date expireAt,
            final ServiceCallback<String> callback
    ) {
        SocialMedium ot = new SocialMedium();
        ot.setType(type);
        ot.setUserId(userId);
        ot.setToken(token);
        ot.setSecret(secret);
        ot.setExpireAt(expireAt);

        m_userApi.addMedium(ot, new RestApiCallback<>(callback));
    }
}
