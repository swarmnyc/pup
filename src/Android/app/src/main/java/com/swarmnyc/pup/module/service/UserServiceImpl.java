package com.swarmnyc.pup.module.service;

import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.module.models.PuPTag;
import com.swarmnyc.pup.module.models.UserDevice;
import com.swarmnyc.pup.module.restapi.RestApiCallback;
import com.swarmnyc.pup.module.restapi.UserRestApi;
import com.swarmnyc.pup.utils.StringUtils;
import com.swarmnyc.pup.module.models.CurrentUserInfo;
import com.swarmnyc.pup.module.models.SocialMedium;
import com.swarmnyc.pup.module.viewmodels.UserRequestResult;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserServiceImpl implements UserService {
    private static final String PASSWORD = "swarmnyc";
    private UserRestApi m_userApi;
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    public UserServiceImpl(UserRestApi userApi) {
        m_userApi = userApi;
    }

    @Override
    public void login(
            final String email, final ServiceCallback<CurrentUserInfo> callback
    ) {
        if (!isRunning.getAndSet(true)) {
            m_userApi.login(
                    email, PASSWORD, new RestApiCallback<UserRequestResult>(null) {
                        @Override
                        public void success(final UserRequestResult userRequestResult, final Response response) {
                            isRunning.set(false);
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
    }

    @Override
    public void register(
            final String email, final String username, String file, String platform ,String cid, final ServiceCallback<CurrentUserInfo> callback
    ) {
        if (!isRunning.getAndSet(true)) {
            TypedFile tf = null;
            if (StringUtils.isNotEmpty(file)) {
                tf = new TypedFile("multipart/form-data", new File(file));
            }

            m_userApi.register(
                    email, PASSWORD, username, tf, platform, cid, new RestApiCallback<UserRequestResult>(null) {
                        @Override
                        public void success(final UserRequestResult userRequestResult, final Response response) {
                            isRunning.set(false);
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

                        @Override
                        public void failure(RetrofitError error) {
                            super.failure(error);
                            isRunning.set(false);
                        }
                    }
            );
        }
    }

    @Override
    public void updatePortrait(final String file, final ServiceCallback<String> callback) {
        if (!isRunning.getAndSet(true)) {
            TypedFile tf = null;
            if (StringUtils.isNotEmpty(file)) {
                tf = new TypedFile("multipart/form-data", new File(file));
            }

            m_userApi.updatePortrait(tf, new RestApiCallback<String>(isRunning, callback){
                @Override
                public void success(String o, Response response) {
                    super.success(o, response);
                    isRunning.set(false);
                }
            });
        }
    }

    @Override
    public void deleteMedium(final String type, final ServiceCallback<String> callback) {
        if (!isRunning.getAndSet(true)) {
            m_userApi.deleteMedium(type, new RestApiCallback<>(isRunning, callback));
        }
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
        if (!isRunning.getAndSet(true)) {
            SocialMedium ot = new SocialMedium();
            ot.setType(type);
            ot.setUserId(userId);
            ot.setToken(token);
            ot.setSecret(secret);
            ot.setExpireAt(expireAt);

            m_userApi.addMedium(ot, new RestApiCallback<>(isRunning, callback));
        }
    }

    @Override
    public void addTag(PuPTag tag, ServiceCallback<String> callback) {
        List<PuPTag> tags = User.current.getTags();
        for (PuPTag t : tags) {
            if (tag.getKey().equals(t.getKey())){
                tags.remove(t);
                break;
            }
        }

        tags.add(tag);
        User.update();

        m_userApi.addTag(tag, new RestApiCallback<>(isRunning, callback));
    }

    @Override
    public void deleteTag(String tagId, ServiceCallback<String> callback) {
        List<PuPTag> tags = User.current.getTags();
        for (PuPTag t : tags) {
            if (tagId.equals(t.getKey())){
                tags.remove(t);
                break;
            }
        }
        User.update();

        m_userApi.deleteTag(tagId, new RestApiCallback<>(isRunning, callback));
    }

    @Override
    public void addDevice(UserDevice device, ServiceCallback<String> callback) {
        User.current.getTags().add(new PuPTag("Notification_Android_" + device.token, device.token));
        User.update();

        m_userApi.addDevice(device, new RestApiCallback<>(isRunning, callback));
    }
}
