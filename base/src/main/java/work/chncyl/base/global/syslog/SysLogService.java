package work.chncyl.base.global.syslog;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.chncyl.base.global.syslog.mapper.SysLogMapper;
import org.springframework.stereotype.Service;

@Service
public class SysLogService extends ServiceImpl<SysLogMapper, work.chncyl.base.global.syslog.SysLog> {

    public void addLog(work.chncyl.base.global.syslog.SysLog dto) {
        baseMapper.insert(dto);
    }
}
