# 게시글 신고 API 명세서

## 📋 개요
게시글 신고 기능은 사용자가 부적절한 게시글을 신고할 수 있도록 하는 API입니다.

## 🗄️ 관련 테이블

### 1. post_reports 테이블
```sql
CREATE TABLE post_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    report_type VARCHAR(50) NOT NULL,
    additional_reason TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    admin_comment TEXT,
    processed_by BIGINT,
    processed_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_post_user (post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (processed_by) REFERENCES users(id)
);
```

### 2. posts 테이블 (신고 수 컬럼)
```sql
-- posts 테이블에 신고 수 컬럼이 추가됨
ALTER TABLE posts ADD COLUMN report_count INT DEFAULT 0;
```

## 🚨 신고 분류 (ReportType)

| 분류 | 코드 | 설명 |
|------|------|------|
| **상업적 광고** | `COMMERCIAL_AD` | 상업적 목적의 광고성 게시글 |
| **욕설/비하** | `ABUSE_DISCRIMINATION` | 욕설, 비하, 차별적 표현 |
| **음란물/불건전 대화** | `PORNOGRAPHY_INAPPROPRIATE` | 음란한 내용이나 불건전한 대화 |
| **유출/사칭/사기** | `LEAK_IMPERSONATION_FRAUD` | 개인정보 유출, 사칭, 사기 |
| **불법촬영물 유통** | `ILLEGAL_VIDEO_DISTRIBUTION` | 불법으로 촬영된 영상물 |
| **게시판 성격에 부적절** | `INAPPROPRIATE_FOR_BOARD` | 게시판 성격에 맞지 않는 내용 |
| **낚시/놀람/도배** | `TROLLING_SPAM` | 의도적인 낚시, 놀람, 도배 |

## 📊 신고 처리 상태 (ReportStatus)

| 상태 | 코드 | 설명 |
|------|------|------|
| **대기중** | `PENDING` | 신고 접수 후 처리 대기 |
| **처리중** | `PROCESSING` | 관리자가 검토 중 |
| **승인** | `APPROVED` | 신고 내용이 적절하여 승인 |
| **거부** | `REJECTED` | 신고 내용이 부적절하여 거부 |

## 🔧 API 엔드포인트

### 1. 게시글 신고
```
POST /api/posts/{postId}/report
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

Request Body:
{
  "reportType": "ABUSE_DISCRIMINATION",
  "additionalReason": "욕설이 포함된 게시글입니다."
}

Response:
{
  "message": "게시글 신고 완료",
  "count": 1,
  "isLiked": false,
  "isReported": true,
  "isBookmarked": false
}
```

### 2. 게시글 신고 목록 조회 (관리자용)
```
GET /api/posts/{postId}/reports
Authorization: Bearer {JWT_TOKEN} (ADMIN 권한 필요)

Response:
{
  "userIds": [1, 2, 3],
  "totalCount": 3
}
```

### 3. 게시글 신고 취소 (관리자용)
```
DELETE /api/posts/{postId}/report/{userId}
Authorization: Bearer {JWT_TOKEN} (ADMIN 권한 필요)

Response:
{
  "message": "신고가 취소되었습니다.",
  "count": 2,
  "isLiked": false,
  "isReported": false,
  "isBookmarked": false
}
```

## 📱 Flutter 연동 예시

### 1. 게시글 신고 함수
```dart
Future<Map<String, dynamic>> reportPost(int postId, String reportType, String additionalReason) async {
  final response = await http.post(
    Uri.parse('$baseUrl/api/posts/$postId/report'),
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer $token',
    },
    body: jsonEncode({
      'reportType': reportType,
      'additionalReason': additionalReason,
    }),
  );
  
  if (response.statusCode == 200) {
    return json.decode(response.body);
  } else {
    throw Exception('신고 처리 실패');
  }
}
```

### 2. 신고 분류 선택 UI
```dart
class ReportDialog extends StatefulWidget {
  final int postId;
  
  @override
  _ReportDialogState createState() => _ReportDialogState();
}

class _ReportDialogState extends State<ReportDialog> {
  String selectedReportType = 'ABUSE_DISCRIMINATION';
  String additionalReason = '';
  
  final Map<String, String> reportTypes = {
    'COMMERCIAL_AD': '상업적 광고',
    'ABUSE_DISCRIMINATION': '욕설/비하',
    'PORNOGRAPHY_INAPPROPRIATE': '음란물/불건전 대화',
    'LEAK_IMPERSONATION_FRAUD': '유출/사칭/사기',
    'ILLEGAL_VIDEO_DISTRIBUTION': '불법촬영물 유통',
    'INAPPROPRIATE_FOR_BOARD': '게시판 성격에 부적절',
    'TROLLING_SPAM': '낚시/놀람/도배',
  };
  
  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text('게시글 신고'),
      content: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          // 신고 분류 선택
          DropdownButtonFormField<String>(
            value: selectedReportType,
            decoration: InputDecoration(labelText: '신고 분류'),
            items: reportTypes.entries.map((entry) {
              return DropdownMenuItem(
                value: entry.key,
                child: Text(entry.value),
              );
            }).toList(),
            onChanged: (value) {
              setState(() {
                selectedReportType = value!;
              });
            },
          ),
          SizedBox(height: 16),
          // 추가 사유 입력
          TextField(
            decoration: InputDecoration(
              labelText: '추가 사유 (선택사항)',
              hintText: '신고 사유를 자세히 설명해주세요.',
            ),
            maxLines: 3,
            onChanged: (value) {
              additionalReason = value;
            },
          ),
        ],
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.of(context).pop(),
          child: Text('취소'),
        ),
        ElevatedButton(
          onPressed: () async {
            try {
              final result = await reportPost(
                widget.postId,
                selectedReportType,
                additionalReason,
              );
              Navigator.of(context).pop();
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text(result['message'])),
              );
            } catch (e) {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('신고 처리 중 오류가 발생했습니다.')),
              );
            }
          },
          child: Text('신고하기'),
        ),
      ],
    );
  }
}
```

### 3. 게시글 상세 페이지에서 신고 버튼
```dart
// 게시글 상세 페이지에 신고 버튼 추가
IconButton(
  icon: Icon(Icons.report),
  onPressed: () {
    showDialog(
      context: context,
      builder: (context) => ReportDialog(postId: widget.postId),
    );
  },
),
```

## 🧪 API 테스트

### curl 명령어로 테스트
```bash
# 게시글 신고
curl -X POST http://localhost:8080/api/posts/24/report \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "reportType": "ABUSE_DISCRIMINATION",
    "additionalReason": "욕설이 포함된 게시글입니다."
  }'

# 관리자용 신고 목록 조회
curl -X GET http://localhost:8080/api/posts/24/reports \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"

# 관리자용 신고 취소
curl -X DELETE http://localhost:8080/api/posts/24/report/1 \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

## ⚠️ 주의사항

1. **중복 신고 방지**: 한 사용자가 같은 게시글을 중복 신고할 수 없습니다.
2. **관리자 권한**: 신고 목록 조회와 취소는 ADMIN 권한이 필요합니다.
3. **신고 분류 필수**: reportType은 반드시 지정해야 합니다.
4. **추가 사유 선택**: additionalReason은 선택사항입니다.
5. **신고 수 자동 증가**: 신고 시 posts 테이블의 report_count가 자동으로 증가합니다.

## 📊 신고 처리 플로우

1. **사용자 신고** → `PENDING` 상태로 저장
2. **관리자 검토** → `PROCESSING` 상태로 변경
3. **처리 완료** → `APPROVED` 또는 `REJECTED` 상태로 변경
4. **처리 결과** → 관리자 코멘트와 함께 저장

## 🔍 추가 기능

- **신고 통계**: 관리자 대시보드에서 신고 통계 확인 가능
- **자동 처리**: 특정 조건에 따른 자동 신고 처리
- **신고 알림**: 관리자에게 신고 접수 알림
- **신고 이력**: 사용자별 신고 이력 관리 