import warnings

warnings.filterwarnings('ignore')

import tensorflow as tf

print(tf.__version__)
print(tf.config.list_physical_devices('GPU'))

import glob
import numpy as np
import tensorflow as tf
from tensorflow.keras import Model, Input
from tensorflow.keras.layers import Dense, Bidirectional, LSTM, TimeDistributed
from tensorflow.keras.callbacks import ModelCheckpoint, EarlyStopping
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.mixed_precision import Policy, set_global_policy

# 혼합 정밀도 정책 설정
policy = Policy('mixed_float16')
set_global_policy(policy)

def data_generator(x_paths, onset_paths, model):
    for x_path, onset_path in zip(x_paths, onset_paths):
        X = np.load(x_path)
        ONSET = np.load(onset_path)

        X = X / np.max(X)  # 정규화

        model.reset_states()  # 한 곡마다 모델 상태 초기화
        for x, onset in zip(X, ONSET):
            yield (x, onset)  # 한 세그먼트씩 반환

def build_model():
    input_layer = Input(batch_input_shape=(10, 100, 264), name='onset_input')
    onset_lstm = Bidirectional(LSTM(128, activation='tanh', return_sequences=True, stateful=True, name='onset_lstm'))(input_layer)
    onset_out = TimeDistributed(Dense(88, activation='sigmoid', kernel_initializer='he_normal', name='onset_output'))(onset_lstm)
    model = Model(inputs=input_layer, outputs=onset_out)

    return model

def train(train_x, train_onset, valid_x, valid_onset):
    train_x_paths, train_onset_paths = [], []
    valid_x_paths, valid_onset_paths = [], []

    for x_path, onset_path in zip(glob.glob(train_x), glob.glob(train_onset)):
        train_x_paths.append(x_path)
        train_onset_paths.append(onset_path)

    for x_path, onset_path in zip(glob.glob(valid_x), glob.glob(valid_onset)):
        valid_x_paths.append(x_path)
        valid_onset_paths.append(onset_path)

    output_signature = (tf.TensorSpec(shape=(100, 264), dtype=tf.float32), tf.TensorSpec(shape=(100, 88), dtype=tf.int8))

    model = build_model()

    train_set = tf.data.Dataset.from_generator(lambda: data_generator(train_x_paths, train_onset_paths, model), output_signature=output_signature)
    valid_set = tf.data.Dataset.from_generator(lambda: data_generator(valid_x_paths, valid_onset_paths, model), output_signature=output_signature)
    train_set = train_set.batch(10, drop_remainder=True).prefetch(tf.data.experimental.AUTOTUNE)
    valid_set = valid_set.batch(10, drop_remainder=True).prefetch(tf.data.experimental.AUTOTUNE)

    checkpoint = ModelCheckpoint('models/onset_detector.h5', monitor='val_loss', verbose=1, save_best_only=True, mode='auto')
    early_stop = EarlyStopping(patience=5, monitor='val_loss', verbose=1, mode='auto')

    optimizer = Adam(learning_rate=0.0005)
    model.compile(loss='binary_crossentropy', optimizer=optimizer, metrics=['accuracy'])
    model.fit(train_set, validation_data=valid_set, epochs=6, shuffle=False, callbacks=[checkpoint, early_stop])
    model.save('models/onset_last.h5')

if __name__ == '__main__':
    train('data/preprocessed/trainX/*.npy', 'data/preprocessed/trainONSET/*.npy', 'data/preprocessed/validX/*.npy', 'data/preprocessed/validONSET/*.npy')


import glob
import numpy as np
import tensorflow as tf
from tensorflow.keras import Model, Input
from tensorflow.keras.layers import Dense, Bidirectional, LSTM, TimeDistributed
from tensorflow.keras.callbacks import ModelCheckpoint, EarlyStopping
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.mixed_precision import Policy, set_global_policy

# 혼합 정밀도 정책 설정
policy = Policy('mixed_float16')
set_global_policy(policy)

def data_generator(x_paths, onset_paths, model):
    for x_path, onset_path in zip(x_paths, onset_paths):
        X = np.load(x_path)
        ONSET = np.load(onset_path)

        X = X / np.max(X)  # 정규화

        model.reset_states()  # 한 곡마다 모델 상태 초기화
        for x, onset in zip(X, ONSET):
            yield (x, onset)  # 한 세그먼트씩 반환

def build_model():
    input_layer = Input(batch_input_shape=(10, 100, 264), name='onset_input')
    onset_lstm = Bidirectional(LSTM(128, activation='tanh', return_sequences=True, stateful=True, name='onset_lstm'))(input_layer)
    onset_out = TimeDistributed(Dense(88, activation='sigmoid', kernel_initializer='he_normal', name='onset_output'))(onset_lstm)
    model = Model(inputs=input_layer, outputs=onset_out)

    return model

def train(train_x, train_onset, valid_x, valid_onset):
    train_x_paths, train_onset_paths = [], []
    valid_x_paths, valid_onset_paths = [], []

    for x_path, onset_path in zip(glob.glob(train_x), glob.glob(train_onset)):
        train_x_paths.append(x_path)
        train_onset_paths.append(onset_path)

    for x_path, onset_path in zip(glob.glob(valid_x), glob.glob(valid_onset)):
        valid_x_paths.append(x_path)
        valid_onset_paths.append(onset_path)

    output_signature = (tf.TensorSpec(shape=(100, 264), dtype=tf.float32), tf.TensorSpec(shape=(100, 88), dtype=tf.int8))

    model = build_model()

    train_set = tf.data.Dataset.from_generator(lambda: data_generator(train_x_paths, train_onset_paths, model), output_signature=output_signature)
    valid_set = tf.data.Dataset.from_generator(lambda: data_generator(valid_x_paths, valid_onset_paths, model), output_signature=output_signature)
    train_set = train_set.batch(10, drop_remainder=True).prefetch(tf.data.experimental.AUTOTUNE)
    valid_set = valid_set.batch(10, drop_remainder=True).prefetch(tf.data.experimental.AUTOTUNE)

    checkpoint = ModelCheckpoint('models/offset_detector.h5', monitor='val_loss', verbose=1, save_best_only=True, mode='auto')
    early_stop = EarlyStopping(patience=5, monitor='val_loss', verbose=1, mode='auto')

    optimizer = Adam(learning_rate=0.0005)
    model.compile(loss='binary_crossentropy', optimizer=optimizer, metrics=['accuracy'])
    model.fit(train_set, validation_data=valid_set, epochs=6, shuffle=False, callbacks=[checkpoint, early_stop])
    model.save('models/offset_last.h5')

if __name__ == '__main__':
    train('data/preprocessed/trainX/*.npy', 'data/preprocessed/trainOFFSET/*.npy', 'data/preprocessed/validX/*.npy', 'data/preprocessed/validOFFSET/*.npy')
