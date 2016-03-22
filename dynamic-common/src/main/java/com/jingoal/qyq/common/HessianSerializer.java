package com.jingoal.qyq.common;

import com.caucho.hessian.io.*;

import java.io.*;

/**
 * Created by lianghb on 16/3/18.
 * Description: Hessian 序列化实现
 */
public class HessianSerializer implements Serializer {

    public byte[] serialize(Object object) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Hessian2Output ho = new Hessian2Output(os);
        try {
            ho.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return os.toByteArray();
    }

    public Object deserialize(byte[] bytes) {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Hessian2Input hi = new Hessian2Input(is);
        try {
            return hi.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
