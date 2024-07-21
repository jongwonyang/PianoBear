import warnings

warnings.filterwarnings('ignore')

import glob
import numpy as np
import tensorflow as tf
from tensorflow.keras.models import load_model

def data_generator(x_paths, onset_paths):
    for x_path, onset_path in zip(x_paths, onset_paths):
        X = np.load(x_path)
        ONSET = np.load(onset_path)

        X = X / np.max(X)  # 정규화

        for x, onset in zip(X, ONSET):
            yield (x, onset)  # 한 세그먼트씩 반환

def evaluate(test_x, test_onset):
    test_x_paths, test_onset_paths = [], []

    for x_path, onset_path in zip(glob.glob(test_x), glob.glob(test_onset)):
        test_x_paths.append(x_path)
        test_onset_paths.append(onset_path)

    output_signature = (tf.TensorSpec(shape=(100, 264), dtype=tf.float32), tf.TensorSpec(shape=(100, 88), dtype=tf.int8))

    batch_size = 10

    test_set = tf.data.Dataset.from_generator(lambda: data_generator(test_x_paths, test_onset_paths), output_signature=output_signature)
    test_set = test_set.batch(batch_size, drop_remainder=True).prefetch(tf.data.experimental.AUTOTUNE)

    model = load_model('models/onset_detector.h5')

    steps_per_epoch = sum([len(np.load(file)) for file in test_x_paths]) // batch_size

    loss, accuracy = model.evaluate(test_set, steps=steps_per_epoch, verbose=2)
    print(f'Test Loss: {loss}')
    print(f'Test Accuracy: {accuracy}')

if __name__ == '__main__':
    evaluate('data/preprocessed/testX/*.npy', 'data/preprocessed/testONSET/*.npy')

import glob
import numpy as np
import tensorflow as tf
from tensorflow.keras.models import load_model

def data_generator(x_paths, offset_paths):
    for x_path, onset_path in zip(x_paths, offset_paths):
        X = np.load(x_path)
        OFFSET = np.load(onset_path)

        X = X / np.max(X)  # 정규화

        for x, onset in zip(X, OFFSET):
            yield (x, onset)  # 한 세그먼트씩 반환

def evaluate(test_x, test_offset):
    test_x_paths, test_offset_paths = [], []

    for x_path, offset_path in zip(glob.glob(test_x), glob.glob(test_offset)):
        test_x_paths.append(x_path)
        test_offset_paths.append(offset_path)

    output_signature = (tf.TensorSpec(shape=(100, 264), dtype=tf.float32), tf.TensorSpec(shape=(100, 88), dtype=tf.int8))

    batch_size = 10

    test_set = tf.data.Dataset.from_generator(lambda: data_generator(test_x_paths, test_offset_paths), output_signature=output_signature)
    test_set = test_set.batch(batch_size, drop_remainder=True).prefetch(tf.data.experimental.AUTOTUNE)

    model = load_model('models/offset_detector.h5')

    steps_per_epoch = sum([len(np.load(file)) for file in test_x_paths]) // batch_size

    loss, accuracy = model.evaluate(test_set, steps=steps_per_epoch, verbose=2)
    print(f'Test Loss: {loss}')
    print(f'Test Accuracy: {accuracy}')

if __name__ == '__main__':
    evaluate('data/preprocessed/testX/*.npy', 'data/preprocessed/testOFFSET/*.npy')