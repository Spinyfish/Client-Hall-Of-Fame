/*
 * This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kevin.utils.render.GL;

import org.lwjgl.util.glu.GLUtessellatorCallbackAdapter;

import static org.lwjgl.opengl.GL11.*;

public class DirectTessCallback extends GLUtessellatorCallbackAdapter {
    public final static DirectTessCallback INSTANCE = new DirectTessCallback();

    @Override
    public void begin(int type) {
        glBegin(type);
    }

    @Override
    public void combine(double[] coords, Object[] data, float[] weight, Object[] outData) {
        double[] combined = new double[6];
        combined[0] = coords[0];
        combined[1] = coords[1];
        combined[2] = coords[2];
        combined[3] = 1;
        combined[4] = 1;
        combined[5] = 1;

        for (int i=0;i < outData.length;i++) {
            outData[i] = new VertexData(combined);
        }
    }

    public void end() {
        glEnd();
    }

    @Override
    public void vertex(Object vertexData) {
        VertexData vertex = (VertexData) vertexData;

        glVertex3f((float)vertex.data[0], (float)vertex.data[1], (float)vertex.data[2]);
    }
}
