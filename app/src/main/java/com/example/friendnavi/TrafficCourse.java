package com.example.friendnavi;

public class TrafficCourse {

    String[] courseType = new String[124];

    public TrafficCourse() {
        courseType[0] = "";
        courseType[1] = "직진";
        courseType[2] = "좌회전";
        courseType[3] = "우회전";
        courseType[4] = "왼쪽 방향";
        courseType[5] = "오른쪽 방향";
        courseType[6] = "U턴";
        courseType[7] = "";
        courseType[8] = "비보호 좌회전";
        courseType[9] = "";
        courseType[10] = "";
        courseType[11] = "좌 8시";
        courseType[12] = "좌 9시";
        courseType[13] = "좌 11시";
        courseType[14] = "우 1시";
        courseType[15] = "우 3시";
        courseType[16] = "우 4시";
        courseType[17] = "";
        courseType[18] = "";
        courseType[19] = "";
        courseType[20] = "";
        courseType[21] = "로터리 직진";
        courseType[22] = "로터리 U턴";
        courseType[23] = "로터리 좌7";
        courseType[24] = "로터리 좌8";
        courseType[25] = "로터리 좌9";
        courseType[26] = "로터리 좌10";
        courseType[27] = "로터리 좌11";
        courseType[28] = "로터리 12";
        courseType[29] = "로터리 우1";
        courseType[30] = "로터리 우2";
        courseType[31] = "로터리 우3";
        courseType[32] = "로터리 우4";
        courseType[33] = "로터리 우5";
        courseType[34] = "로터리 6";
        courseType[35] = "";
        courseType[36] = "";
        courseType[37] = "";
        courseType[38] = "";
        courseType[39] = "";
        courseType[40] = "";
        courseType[41] = "좌로 진입";
        courseType[42] = "우로 진입";
        courseType[43] = "";
        courseType[44] = "";
        courseType[45] = "";
        courseType[46] = "";
        courseType[47] = "휴게소 진입";
        courseType[48] = "페리항로 진입";
        courseType[49] = "페리항로 진출";
        courseType[50] = "고속도로 진입";
        courseType[51] = "고속도로 진출";
        courseType[52] = "도시고속 진입";
        courseType[53] = "도시고속 진출";
        courseType[54] = "분기 진입";
        courseType[55] = "고가 진입";
        courseType[56] = "지하 진입";
        courseType[57] = "좌 고속 진입";
        courseType[58] = "좌 고속 진출";
        courseType[59] = "좌 도시고속 진입";
        courseType[60] = "좌 도시고속 진출";
        courseType[61] = "";
        courseType[62] = "좌 고가 진입";
        courseType[63] = "좌 고가 옆길";
        courseType[64] = "좌 지하 진입";
        courseType[65] = "좌 지하 옆길";
        courseType[66] = "우 고속 진입";
        courseType[67] = "우 고속 진출";
        courseType[68] = "우 도시고속 진입";
        courseType[69] = "우 도시고속 진출";
        courseType[70] = "";
        courseType[71] = "우 고가 진입";
        courseType[72] = "우 고가 옆길";
        courseType[73] = "우 지하 진입";
        courseType[74] = "우 지하 옆길";
        courseType[75] = "전용 진입";
        courseType[76] = "좌 전용 진입";
        courseType[77] = "우 전용 진입";
        courseType[78] = "전용 진출";
        courseType[79] = "좌 전용 진출";
        courseType[80] = "우 전용 진출";
        courseType[81] = "좌로 합류";
        courseType[82] = "우로 합류";
        courseType[83] = "";
        courseType[84] = "";
        courseType[85] = "";
        courseType[86] = "";
        courseType[87] = "경유지";
        courseType[88] = "도착지";
        courseType[89] = "";
        courseType[90] = "";
        courseType[91] = "회전교차 직진";
        courseType[92] = "회전교차 U턴";
        courseType[93] = "회전교차 좌7";
        courseType[94] = "회전교차 좌8";
        courseType[95] = "회전교차 좌9";
        courseType[96] = "회전교차 좌10";
        courseType[97] = "회전교차 좌11";
        courseType[98] = "회전교차 12";
        courseType[99] = "회전교차 우1";
        courseType[100] = "회전교차 우2";
        courseType[101] = "회전교차 우3";
        courseType[102] = "회전교차 우4";
        courseType[103] = "회전교차 우5";
        courseType[104] = "회전교차 6";
        courseType[105] = "";
        courseType[106] = "";
        courseType[107] = "";
        courseType[108] = "";
        courseType[109] = "";
        courseType[110] = "";
        courseType[111] = "";
        courseType[112] = "";
        courseType[113] = "";
        courseType[114] = "";
        courseType[115] = "";
        courseType[116] = "";
        courseType[117] = "";
        courseType[118] = "";
        courseType[119] = "";
        courseType[120] = "";
        courseType[121] = "톨게이트";
        courseType[122] = "하이패스";
        courseType[123] = "원톨링";
    }

    public String getCourse(int type) {
        return courseType[type];
    }

}
