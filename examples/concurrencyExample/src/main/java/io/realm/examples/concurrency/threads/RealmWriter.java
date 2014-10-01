/*
 * Copyright 2014 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm.examples.concurrency.threads;

import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import io.realm.examples.concurrency.model.Dog;
import io.realm.examples.concurrency.model.Person;

public class RealmWriter extends Thread implements KillableThread {

    public static final String TAG = RealmWriter.class.getName();

    private Context context = null;

    private boolean mRunning = true;

    private int mInsertCount = 0;

    public RealmWriter(Context context) {
        this.context = context;
    }

    public void run() {
        Realm realm = Realm.getInstance(context);

        int iterCount = 0;

        while (iterCount < mInsertCount && mRunning) {
            realm.beginTransaction();

            Person person = realm.createObject(Person.class);
            person.setName("Foo" + iterCount);
            person.setAge(iterCount % 20 + (50 - 20));

            //Add a dog to every 50th person
            if(iterCount % 50 == 0) {
                Dog dog = realm.createObject(Dog.class);
                dog.setName("Foo" + iterCount + "Fido");
                person.setDog(dog);
            }

            iterCount++;
            realm.commitTransaction();

            if ((iterCount % 1000) == 0) {
                Log.d(TAG, "WriteOperation#: " + iterCount + "," + Thread.currentThread().getName());
            }
        }
    }

    @Override
    public void terminate() {
        mRunning = false;
    }

    @SuppressWarnings("UnusedDeclaration")
    public int getInsertCount() {
        return mInsertCount;
    }

    public void setInsertCount(int count) {
        this.mInsertCount = count;
    }
}
