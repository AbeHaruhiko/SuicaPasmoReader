/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kazzz.iso15693;


import net.kazzz.AbstractNfcTagFragment;
import net.kazzz.iso15693.command.ReadMultipleBlocksResponse;
import net.kazzz.iso15693.command.SystemInformationResponse;
import net.kazzz.iso15693.lib.ISO15693Lib.MemorySizeInfo;
import net.kazzz.iso15693.lib.ISO15693Lib.ResponseFormat;
import net.kazzz.nfc.NfcTag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcV;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * NfcでISO15963 Tagを読み込むためのフラグメントを提供します
 * 
 * @author Kazzz.
 * @date 2011/06/24
 * @since Android API Level 9
 *
 */

public class ISO15693TagFragment extends AbstractNfcTagFragment {
    public static final String TAG = "ISO15693TagFragment";
    
    /**
     * @param activity
     */
    public ISO15693TagFragment(FragmentActivity activity) {
        super(activity, ISO15693TagFragment.TAG);
        
        //ISO15693は NFC-V 
        mTechList = new String[][]{ new String[] { Ndef.class.getName(), NfcV.class.getName() }};
    }
    /**
     * ISO15693Tagクラスのインスタンスを生成します
     * @return ISO15693Tag 生成したISO15693Tagクラスのインスタンスが戻ります
     */
    public ISO15693Tag createTag() {
        return new ISO15693Tag(mNfcTag);
    }
    
    /* (non-Javadoc)
     * @see net.net.kazzz.AbstractNfcTagFragment#createNfcTag()
     */
    @Override
    public NfcTag createNfcTag() {
        return this.createTag();
    }
    /* (non-Javadoc)
     * @see net.net.kazzz.NfcTagFragment#dumpTagData()
     */
    @Override
    public String dumpTagData() {
        StringBuilder sb = new StringBuilder();
        try {
            // ISO15693Tag 
            ISO15693Tag tag = this.createTag();
            if ( tag != null ) {
                sb.append("\n");
                sb.append("ISO15693 デバイスです");
                sb.append("\n-----------------------------------------");
                sb.append("\n");
                ResponseFormat rf = tag.getSystemInformation();
                sb.append(rf.toString());
                sb.append("\n");
                
                //全てのブロックを読む
                SystemInformationResponse sysInfo = tag.getSystemInformation();
                if ( sysInfo == null || sysInfo.hasError()) {
                    throw new ISO15693Exception(
                            "ISO15693 デバイスからシステム情報を取得できませんでした : " 
                                    + sysInfo.getErrorCode());
                }
                
                final MemorySizeInfo memInfo = sysInfo.getMemoryInfo();
                if ( memInfo == null || memInfo.getNumberOfBlocks() == 0) {
                    throw new ISO15693Exception("ISO15693 メモリサイズ情報を取得できませんでした");
                }        
                ReadMultipleBlocksResponse resp = 
                        tag.readMultipleBlocks((byte)0, memInfo.getBlockSize(), memInfo.getNumberOfBlocks());
                sb.append("  " + resp.toString());
                sb.append("\n----------------------------------------");
                sb.append("\n");

                
            } else {
                sb.append("デバイスの読み込みに失敗しました");
            }
            
        } catch (Exception e) {
            String result = sb.toString();
            Log.d(TAG, result);
            e.printStackTrace();
            return result;
        }
        String result = sb.toString();
        Log.d(TAG, result);
        return result;
    }
}
