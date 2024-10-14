/*
 * Copyright (C) 2024 Hedera Hashgraph, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swirlds.state.test.fixtures.merkle;

import com.hedera.pbj.runtime.Codec;
import com.hedera.pbj.runtime.ParseException;
import com.hedera.pbj.runtime.io.ReadableSequentialData;
import com.hedera.pbj.runtime.io.WritableSequentialData;
import com.swirlds.state.test.fixtures.StringRecord;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * This is a test-only class used exclusively by MerkleTestBase.
 * <strong>This class should not be used elsewhere.</strong>
 * The use of Codec implementations is not ideal, and we have the protobuf ProtoString as
 * a replacement.  When time permits this class needs to be removed and all usages replaced
 * with ProtoString and the ProtoString.PROTOBUF codec.
 * @deprecated Use ProtoString and ProtoString.PROTOBUF instead of String and this codec.
 */
@Deprecated
@SuppressWarnings("Singleton")
public class TestStringRecordCodec implements Codec<StringRecord> {
    public static final TestStringRecordCodec SINGLETON = new TestStringRecordCodec();

    private TestStringRecordCodec() {}

    @NonNull
    @Override
    public StringRecord parse(final @NonNull ReadableSequentialData input, final boolean strictMode, final int maxDepth)
            throws ParseException {
        Objects.requireNonNull(input);
        final var len = input.readInt();
        return len == 0
                ? new StringRecord("")
                : new StringRecord(input.readBytes(len).asUtf8String());
    }

    @Override
    public void write(final @NonNull StringRecord item, final @NonNull WritableSequentialData output)
            throws IOException {
        Objects.requireNonNull(item);
        Objects.requireNonNull(output);
        final byte[] bytes = item.value().getBytes(StandardCharsets.UTF_8);
        output.writeInt(bytes.length);
        output.writeBytes(bytes);
    }

    @Override
    public int measure(final @NonNull ReadableSequentialData input) throws ParseException {
        return input.readInt() + Integer.BYTES;
    }

    @Override
    public int measureRecord(final StringRecord item) {
        Objects.requireNonNull(item);
        final byte[] bytes = item.value().getBytes(StandardCharsets.UTF_8);
        return bytes.length + Integer.BYTES;
    }

    @Override
    public boolean fastEquals(final @NonNull StringRecord item, final @NonNull ReadableSequentialData input)
            throws ParseException {
        Objects.requireNonNull(item);
        Objects.requireNonNull(input);
        return item.equals(parse(input));
    }
}
