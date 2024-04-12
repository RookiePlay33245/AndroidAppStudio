/*
 * This file is part of Android AppStudio [https://github.com/TS-Code-Editor/AndroidAppStudio].
 *
 * License Agreement
 * This software is licensed under the terms and conditions outlined below. By accessing, copying, modifying, or using this software in any way, you agree to abide by these terms.
 *
 * 1. **  Copy and Modification Restrictions  **
 *    - You are not permitted to copy or modify the source code of this software without the permission of the owner, which may be granted publicly on GitHub Discussions or on Discord.
 *    - If permission is granted by the owner, you may copy the software under the terms specified in this license agreement.
 *    - You are not allowed to permit others to copy the source code that you were allowed to copy by the owner.
 *    - Modified or copied code must not be further copied.
 * 2. **  Contributor Attribution  **
 *    - You must attribute the contributors by creating a visible list within the application, showing who originally wrote the source code.
 *    - If you copy or modify this software under owner permission, you must provide links to the profiles of all contributors who contributed to this software.
 * 3. **  Modification Documentation  **
 *    - All modifications made to the software must be documented and listed.
 *    - the owner may incorporate the modifications made by you to enhance this software.
 * 4. **  Consistent Licensing  **
 *    - All copied or modified files must contain the same license text at the top of the files.
 * 5. **  Permission Reversal  **
 *    - If you are granted permission by the owner to copy this software, it can be revoked by the owner at any time. You will be notified at least one week in advance of any such reversal.
 *    - In case of Permission Reversal, if you fail to acknowledge the notification sent by us, it will not be our responsibility.
 * 6. **  License Updates  **
 *    - The license may be updated at any time. Users are required to accept and comply with any changes to the license.
 *    - In such circumstances, you will be given 7 days to ensure that your software complies with the updated license.
 *    - We will not notify you about license changes; you need to monitor the GitHub repository yourself (You can enable notifications or watch the repository to stay informed about such changes).
 * By using this software, you acknowledge and agree to the terms and conditions outlined in this license agreement. If you do not agree with these terms, you are not permitted to use, copy, modify, or distribute this software.
 *
 * Copyright © 2024 Dev Kumar
 */

package com.tscodeeditor.android.appstudio.helper;

import android.code.editor.common.interfaces.FileDeleteListener;
import android.code.editor.common.utils.FileDeleteUtils;
import com.tscodeeditor.android.appstudio.activities.BaseActivity;
import com.tscodeeditor.android.appstudio.block.model.FileModel;
import com.tscodeeditor.android.appstudio.exception.ProjectCodeBuildException;
import com.tscodeeditor.android.appstudio.listener.ProjectCodeBuildListener;
import java.io.File;
import java.util.concurrent.Executors;

public final class ProjectCodeBuilder {

  public void buildProjectCode(
      File rootDestination,
      FileModel model,
      BaseActivity activity,
      ProjectCodeBuildListener listener,
      boolean shouldCleanBeforeBuild) {
    Executors.newSingleThreadExecutor()
        .execute(
            () -> {
              if (listener != null) listener.onBuildStart();

              if (rootDestination.exists()) {
                if (rootDestination.isDirectory()) {

                  if (shouldCleanBeforeBuild) {
                    if (listener != null) {
                      listener.onBuildProgressLog("Cleaning destination folder...");
                    }
                    if (cleanFile(rootDestination)) {
                      if (listener != null) {
                        ProjectCodeBuildException exception = new ProjectCodeBuildException();
                        exception.setMessage("Failed to clean destination folder");
                        listener.onBuildFailed(exception);
                      }
                    }
                  }

                } else {

                  if (listener != null) {
                    StringBuilder log = new StringBuilder();
                    log.append(
                        "Destination directory in which file/folder was about to generate is an file but expected folder");
                    log.append("\n");
                    log.append("Removing the file...");
                    listener.onBuildProgressLog(log.toString());
                  }

                  rootDestination.delete();

                  if (listener != null) {
                    listener.onBuildProgressLog("Creating destination folder...");
                  }
                  rootDestination.mkdirs();

                  if (listener != null) {
                    listener.onBuildProgressLog("Created destination folder...");
                  }
                }
              } else {
                if (listener != null) {
                  listener.onBuildProgressLog("Destination folder doesn't exists, Creating new...");
                }
                rootDestination.mkdirs();
                if (listener != null) {
                  listener.onBuildProgressLog("Created Destination folder");
                }
              }

              // TODO: Generate Code...
            });
  }

  public void buildProjectCode(
      File rootDestination,
      FileModel model,
      BaseActivity activity,
      boolean shouldCleanBeforeBuild) {
    buildProjectCode(rootDestination, model, activity, null, shouldCleanBeforeBuild);
  }

  private boolean cleanFile(File file) {
    if (!file.exists()) {
      file.mkdirs();
      return true;
    }
    if (file.isFile()) {
      return file.delete();
    } else {
      if (file.listFiles().length == 0) {
        return true;
      } else {
        for (File subFile : file.listFiles()) {
          if (!cleanFile(subFile)) {
            return false;
          }
        }
        return true;
      }
    }
  }
}