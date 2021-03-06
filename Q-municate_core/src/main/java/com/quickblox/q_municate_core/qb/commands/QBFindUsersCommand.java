package com.quickblox.q_municate_core.qb.commands;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.q_municate_core.core.command.ServiceCommand;
import com.quickblox.q_municate_core.models.User;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.ConstsCore;
import com.quickblox.q_municate_core.utils.FriendUtils;
import com.quickblox.users.QBUsers;
import com.quickblox.content.QBContent;
import com.quickblox.users.model.QBUser;

import java.util.Collection;

public class QBFindUsersCommand extends ServiceCommand {

    public QBFindUsersCommand(Context context, String successAction, String failAction) {
        super(context, successAction, failAction);
    }

    public static void start(Context context, QBUser currentUser, String constraint, int page) {


        Intent intent = new Intent(QBServiceConsts.FIND_USERS_ACTION, null, context, QBService.class);

        intent.putExtra(QBServiceConsts.EXTRA_USER, currentUser);
        intent.putExtra(QBServiceConsts.EXTRA_CONSTRAINT, constraint);
        intent.putExtra(QBServiceConsts.EXTRA_PAGE, page);

        Log.d("Details :", " Starting Executer Service ...  ");


        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {


        String constraint = (String) extras.getSerializable(QBServiceConsts.EXTRA_CONSTRAINT);
        QBUser currentUser = (QBUser) extras.getSerializable(QBServiceConsts.EXTRA_USER);
        int page = extras.getInt(QBServiceConsts.EXTRA_PAGE);

        Log.d("Details :", " CALLED IN BUNDULE WHEN SEARCHING for " + constraint);


        QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
        requestBuilder.setPage(page);
        requestBuilder.setPerPage(ConstsCore.FL_FRIENDS_PER_PAGE);

        //        TODO temp
        //        ArrayList<GenericQueryRule> ruleList = new ArrayList<GenericQueryRule>();
        //        ruleList.add(new GenericQueryRule("order", "asc+string+full_name"));
        //        requestBuilder.setRules(ruleList);

        Bundle requestParams = new Bundle();
        Collection<QBUser> userList = QBUsers.getUsersByFullName(constraint, requestBuilder, requestParams);

        
        Collection<User> userCollection = FriendUtils.createUsersList(userList);
        userCollection.remove(FriendUtils.createUser(currentUser));

        Bundle params = new Bundle();
        params.putString(QBServiceConsts.EXTRA_CONSTRAINT, constraint);
        params.putInt(QBServiceConsts.EXTRA_TOTAL_ENTRIES, requestParams.getInt(QBServiceConsts.EXTRA_TOTAL_ENTRIES));
        params.putSerializable(QBServiceConsts.EXTRA_USERS, (java.io.Serializable) userCollection);

        Log.d("Details :", " CALLED IN BUNDULE WHEN SEARCHING got values: " + params);


        return params;
    }
}