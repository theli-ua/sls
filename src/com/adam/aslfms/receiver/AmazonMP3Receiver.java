/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.adam.aslfms.receiver;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.adam.aslfms.util.Track;
import com.adam.aslfms.util.Util;
import java.util.*;

/**
 * A BroadcastReceiver for intents sent by the Amazon Music music player
 *
 * @see AbstractPlayStatusReceiver
 *
 * @author Anton Romanov <theli.ua@gmail.com>
 * @since 1.4.7
 */
public class AmazonMP3Receiver extends AbstractPlayStatusReceiver {

    static final String APP_PACKAGE = "com.amazon.mp3";
    static final String APP_NAME = "Amazon Music";

    static final String ACTION_AMZN_META = "com.amazon.mp3.metachanged";
    static final String ACTION_AMZN_PLAYSTATE = "com.amazon.mp3.playstatechanged";

    static final String TAG = "AmazonMP3Receiver";
    static private Track track = null;

    public static void dumpIntent(Bundle bundle){
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            Log.e(TAG,"Dumping Intent start");
            while (it.hasNext()) {
                String key = it.next();
                Log.e(TAG,"[" + key + "=" + bundle.get(key)+"]");
            }
            Log.e(TAG,"Dumping Intent end");
        }
    }

    @Override
    protected void parseIntent(Context ctx, String action, Bundle bundle) {

        MusicAPI musicAPI = MusicAPI.fromReceiver(
            ctx, APP_NAME, APP_PACKAGE, null, false);
        setMusicAPI(musicAPI);

        if (action == ACTION_AMZN_PLAYSTATE)
        {
            setTrack(track);
            int state = bundle.getInt("com.amazon.mp3.playstate");
            if (state == 0) {
                setState(Track.State.COMPLETE);
                Log.d(TAG,"Setting state to COMPLETE");
            }
            else if (state == 1) {
                setState(Track.State.PAUSE);
                Log.d(TAG,"Setting state to PAUSE");
            }
            else if (state == 2) {
                setState(Track.State.START);
                Log.d(TAG,"Setting state to START");
            }
            else if (state == 3) {
                setState(Track.State.RESUME);
                Log.d(TAG,"Setting state to RESUME");
            }
        }
        else if (action == ACTION_AMZN_META)
        {
            Track.Builder b = new Track.Builder();
            b.setMusicAPI(musicAPI);
            b.setWhen(Util.currentTimeSecsUTC());

            b.setArtist(bundle.getString("com.amazon.mp3.artist"));
            b.setAlbum(bundle.getString("com.amazon.mp3.album"));
            b.setTrack(bundle.getString("com.amazon.mp3.track"));

            track = b.build();
        }
        else 
        {
            dumpIntent(bundle);

        }

    }
}
